package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.Optional;

public class MetadataUtils {

    public static final String ANNOTATION_STORAGE_ACCOUNT_NAME = "fintlabs.no/storage-account-name";
    public static final String LABEL_ORG_ID = "fintlabs.no/org-id";
    public static final String LABEL_TEAM = "fintlabs.no/team";

    public static Optional<String> getStorageAccountName(HasMetadata crd) {
        return Optional.ofNullable(crd.getMetadata().getAnnotations().get(ANNOTATION_STORAGE_ACCOUNT_NAME));
    }

    public static Optional<String> getOrgId(HasMetadata crd) {
        return Optional.ofNullable(crd.getMetadata().getLabels().get(LABEL_ORG_ID));
    }

    public static Optional<String> getTeam(HasMetadata crd) {
        return Optional.ofNullable(crd.getMetadata().getLabels().get(LABEL_TEAM));
    }
}
