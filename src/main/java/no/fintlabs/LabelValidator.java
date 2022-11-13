package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to use for validating FLAIS mandatory CRD setup.
 */
@Slf4j
public class LabelValidator {

    /**
     * List of mandatory labels.
     */
    public static List<String> MANDATORY_LABELS = Arrays.asList(
            "app.kubernetes.io/name",
            "app.kubernetes.io/instance",
            "app.kubernetes.io/version",
            "app.kubernetes.io/component",
            "app.kubernetes.io/part-of",
            "fintlabs.no/team",
            "fintlabs.no/org-id");

    /**
     * Checks if the CRD has all the mandatory labels set.
     *
     * @param crd The CRD to validate
     *
     * @throws IllegalArgumentException is thrown if any mandatory labels is missing.
     */
    public static void validate(HasMetadata crd) {
        log.debug("Validating CRD");
        List<String> missingLabels = new ArrayList<>(MANDATORY_LABELS);
        missingLabels.removeAll(crd.getMetadata().getLabels().keySet());

        if (missingLabels.size() > 0) {
            throw new IllegalArgumentException("The following mandatory labels is missing: \n- " + String.join("\n- ", missingLabels));
        }
    }
}
