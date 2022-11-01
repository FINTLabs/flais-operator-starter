package no.fintlabs.testmodels;


import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;
import no.fintlabs.FlaisCrd;

@Group("test.no")
@Version("v1alpha1")
@Kind("Test")
public class TestCrd extends FlaisCrd<TestSpec> {
}
