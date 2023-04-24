package no.fintlabs;


import io.javaoperatorsdk.operator.processing.dependent.workflow.WorkflowBuilder;

/**
 * FLAIS workflow.
 *
 * @param <T> the class providing the CustomResource
 * @param <S> the class providing the {@code Spec} part of this CustomResource
 */
public abstract class FlaisWorkflow<T extends FlaisCrd<S>, S extends FlaisSpec> extends WorkflowBuilder<T> {

}
