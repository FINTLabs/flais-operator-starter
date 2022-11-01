package no.fintlabs;

import io.fabric8.kubernetes.client.CustomResource;

/**
 * @param <S> the class providing the {@code Spec} part of this CustomResource
 */
public abstract class FlaisCrd<S extends FlaisSpec> extends CustomResource<S, FlaisStatus>  {
    @Override
    protected FlaisStatus initStatus() {
        return new FlaisStatus();
    }
}
