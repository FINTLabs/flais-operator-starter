package no.fintlabs.testmodels;

import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import no.fintlabs.FlaisReconiler;
import no.fintlabs.FlaisWorkflow;

import java.util.List;

public class TestReconciler extends FlaisReconiler<TestCrd, TestSpec> {
    public TestReconciler(FlaisWorkflow<TestCrd, TestSpec> workflow,
                          List<? extends DependentResource<?, TestCrd>> eventSourceProviders,
                          List<? extends Deleter<TestCrd>> deleters) {
        super(workflow, eventSourceProviders, deleters);
    }
}
