package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;

public interface HasSecret<P extends FlaisCrd<S>, S extends FlaisSpec> {

     default boolean hasSecret() {
          return false;
     }

     default String getSecretName(HasMetadata primary) {
          return null;
     }

     default boolean shouldBeIncluded(P primary) {
          return true;
     }
}
