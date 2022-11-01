package no.fintlabs;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * FLAIS default status object.
 */
@Getter
@Setter
public class FlaisStatus extends ObservedGenerationAwareStatus {
    private List<String> dependentResourceStatus = new ArrayList<>();
    private String errorMessage;
}
