package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

/**
 * Represents a Kubernetes object.
 * <p>
 * If this resource is dependent of any other dependent resources you should call {@code dependsOn} in the constructor.
 *
 * @param <T> the class providing a kubernetes model.
 * @param <C> the class providing the CRD type.
 * @param <S> the class providing the spec of the CRD.
 */
public abstract class FlaisKubernetesDependentResource<T extends HasMetadata, C extends FlaisCrd<S>, S extends FlaisSpec> extends CRUDKubernetesDependentResource<T, C>
        implements HasSecret<C, S> {

    private final FlaisWorkflow<C, S> workflow;
    private final Condition<T, C> condition;

    public FlaisKubernetesDependentResource(Class<T> resourceType, FlaisWorkflow<C, S> workflow, Condition<T, C> condition, KubernetesClient kubernetesClient) {
        super(resourceType);
        this.workflow = workflow;
        this.condition = condition;
        workflow.addDependentResource(this);
        workflow.withReconcilePrecondition(condition);
        client = kubernetesClient;
    }

    public FlaisKubernetesDependentResource(Class<T> resourceType, FlaisWorkflow<C, S> workflow, KubernetesClient kubernetesClient) {
        super(resourceType);
        this.workflow = workflow;
        this.condition = new DefaultCondition<>();
        workflow.addDependentResource(this);
        workflow.withReconcilePrecondition(condition);
        client = kubernetesClient;
    }

    /**
     * If the dependent resource depends on other dependent resources you can add a list of dependent resources here.
     * <p>
     * You typically inject the resources you depend on in the constructor and add them with this method.
     * <pre>
     * {@code @Slf4j}
     * {@code @Component }
     * public class FintClientSecretDependentResource
     *         extends FlaisKubernetesDependentResource<Secret, FintClientCrd, FintClientSpec> {
     * *
     *     public FintClientSecretDependentResource(..., FintClientDependentResource fintClientDependentResource, ...) {
     *
     *         super(Secret.class, workflow, kubernetesClient);
     *         dependsOn(fintClientDependentResource);
     *         ...
     *         );
     *     }
     * </pre>
     *
     * @param dependentResources A list of dependent resources.
     */
    @SuppressWarnings({"rawtypes"})
    public void dependsOn(DependentResource... dependentResources) {
        workflow.dependsOn(dependentResources);
    }


//    /**
//     * If you want the dependent resource to only be created on a specific condition you can use this method to add
//     * a precondition.
//     *
//     * @param precondition The condition. Extends the  {@link Condition} interface.
//     */
//    //public void withPrecondition(Condition<T, C> precondition) {
//        workflow.withReconcilePrecondition(precondition);
//    }

}
