package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;

/**
 * Represents a Kubernetes object.
 * <p>
 * If this resource is dependent of any other dependent resources you should call {@code dependsOn} in the constructor.
 *
 * @param <T> the class providing a kubernetes model.
 * @param <C> the class providing the CRD type.
 * @param <S> the class providing the spec of the CRD.
 */
public abstract class FlaisKubernetesDependentResource<T extends HasMetadata, C extends FlaisCrd<S>, S extends FlaisSpec> extends CRUDKubernetesDependentResource<T, C> {

    private final FlaisWorkflow<C, S> workflow;

    public FlaisKubernetesDependentResource(Class<T> resourceType, FlaisWorkflow<C, S> workflow, KubernetesClient kubernetesClient) {
        super(resourceType);
        this.workflow = workflow;
        workflow.addDependentResource(this);
        client = kubernetesClient;
    }

    @SuppressWarnings({"rawtypes"})
    public void dependsOn(DependentResource... dependentResources) {
        workflow.dependsOn(dependentResources);
    }

}
