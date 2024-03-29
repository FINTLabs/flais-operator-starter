package no.fintlabs;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.api.reconciler.dependent.ReconcileResult;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Workflow;
import io.javaoperatorsdk.operator.processing.dependent.workflow.WorkflowReconcileResult;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extending this class will give you the default FLAIS reconiler behavior.
 *
 * @param <T> the class representing the CustomResource object
 * @param <S> the class providing the {@code Spec} part of this CustomResource
 */
@Slf4j
public abstract class FlaisReconiler<T extends FlaisCrd<S>, S extends FlaisSpec>
        implements Reconciler<T>,
        Cleaner<T>,
        ErrorStatusHandler<T>,
        EventSourceInitializer<T> {

    private final FlaisWorkflow<T, S> workflow;
    private final List<? extends DependentResource<?, T>> eventSourceProviders;
    private final List<? extends Deleter<T>> deleters;

    public FlaisReconiler(FlaisWorkflow<T, S> workflow,
                          List<? extends DependentResource<?, T>> eventSourceProviders,
                          List<? extends Deleter<T>> deleters) {
        this.workflow = workflow;
        this.eventSourceProviders = eventSourceProviders;
        this.deleters = deleters;
    }


    @Override
    public UpdateControl<T> reconcile(T resource,
                                      Context<T> context) {

        LabelValidator.validate(resource);

        Workflow<T> fileShareWorkflow = workflow.build();
        log.debug("Reconciling {} dependent resources", fileShareWorkflow.getDependentResourcesByName().size());
        WorkflowReconcileResult reconcile = fileShareWorkflow.reconcile(resource, context);

        return updateStatus(reconcile, resource, new FlaisStatus());
    }

    public UpdateControl<T> updateStatus(WorkflowReconcileResult reconcile, T resource, FlaisStatus status) {
        if (isCreated(reconcile)) {
            resource.setStatus(createStatus(reconcile, status));
            return UpdateControl.updateResourceAndStatus(resource);
        }

        if (hasUpdates(reconcile)) {

            resource.setStatus(createStatus(reconcile, status));
            return UpdateControl.patchStatus(resource);
        }

        return UpdateControl.noUpdate();
    }

    public boolean hasUpdates(WorkflowReconcileResult reconcile) {
        return reconcile.getReconcileResults().values().stream().map(reconcileResult -> reconcileResult.getSingleOperation().name())
                .filter(s -> s.equals(ReconcileResult.Operation.NONE.name()))
                .count() != reconcile.getReconciledDependents().size();
    }

    public boolean isCreated(WorkflowReconcileResult reconcile) {
        return reconcile.getReconcileResults().values().stream().map(reconcileResult -> reconcileResult.getSingleOperation().name())
                .filter(s -> s.equals(ReconcileResult.Operation.CREATED.name()))
                .count() == reconcile.getReconciledDependents().size();
    }

    public FlaisStatus createStatus(WorkflowReconcileResult reconcile, FlaisStatus status) {
        List<String> results = new ArrayList<>();
        reconcile.getReconcileResults()
                .forEach((dependentResource, reconcileResult) -> results.add(dependentResource.resourceType().getSimpleName() + " -> " + reconcileResult.getSingleOperation().name()));
        status.setDependentResourceStatus(results);

        return status;

    }

    @Override
    public DeleteControl cleanup(T resource, Context<T> context) {
        deleters.forEach(dr -> dr.delete(resource, context));
        return DeleteControl.defaultDelete();
    }

    @Override
    public ErrorStatusUpdateControl<T> updateErrorStatus(T resource, Context<T> context, Exception e) {
        FlaisStatus fileShareStatus = new FlaisStatus();
        fileShareStatus.setErrorMessage(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        resource.setStatus(fileShareStatus);
        return ErrorStatusUpdateControl.updateStatus(resource);
    }

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<T> context) {
        Map<String, EventSource> eventSources = new HashMap<>(eventSourceProviders.size());
        eventSourceProviders
                .forEach(dependentResource -> eventSources.put(dependentResource.getClass().getSimpleName(), dependentResource.eventSource(context).orElseThrow()));

        return eventSources;
    }


}
