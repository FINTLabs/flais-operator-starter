package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

public class DefaultCondition<T, P extends HasMetadata> implements Condition<T, P> {
    @Override
    public boolean isMet(P r, T t, Context<P> context) {
        return true;
    }
}
