package no.fintlabs;

import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.api.reconciler.dependent.EventSourceProvider;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.external.PerResourcePollingDependentResource;

public abstract class FlaisExternalDependentResource<T, C extends FlaisCrd<S>, S extends FlaisSpec> extends PerResourcePollingDependentResource<T, C>
        implements EventSourceProvider<C>,
        Creator<T, C>,
        Deleter<C> {

    private final FlaisWorkflow<C, S> workflow;
    public FlaisExternalDependentResource(Class<T> resourceType, FlaisWorkflow<C, S> workflow) {
        super(resourceType);
        this.workflow = workflow;

        workflow.addDependentResource(this);
    }

    @SuppressWarnings({"rawtypes"})
    public void dependsOn(DependentResource... dependentResources) {
        workflow.dependsOn(dependentResources);
    }


}
