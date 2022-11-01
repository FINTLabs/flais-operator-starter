package no.fintlabs;

import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.api.reconciler.dependent.EventSourceProvider;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.external.PerResourcePollingDependentResource;

/**
 * Representing an external dependent resource in FLAIS. This can e.g. be an Azure Storage account.
 *
 * @param <T> the class providing external model.
 * @param <C> the class providing the CRD type.
 * @param <S> the class providing the spec of the CRD.
 * @implNote It automatically polls the external source every 5 second to get the latest version. You can
 * change the polling interval by setting the {@code setPollingPeriod} in the constructor. If this resource is
 * dependent of any other dependent resources you should call {@code dependsOn} in the constructor.
 */
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
