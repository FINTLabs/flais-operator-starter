package no.fintlabs;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.javaoperatorsdk.operator.api.config.ResourceClassResolver;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Since our Reconciler is inhereting an abstract class we need to write our own resolver.
 * See <a href="https://github.com/java-operator-sdk/operator-framework-spring-boot-starter/blob/main/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/NaiveResourceClassResolver.java">NaiveResourceClassResolver.class</a>
 * and <a href="https://github.com/java-operator-sdk/operator-framework-spring-boot-starter/blob/da11ad911a6af0243225969f95445373f961fce9/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorAutoConfiguration.java#L102">OperatorAutoConfiguration.java</a>
 * for the default resolver.
 * <p>
 * If flais.operator.resolve-crd-class-strategy has any other value than FLAIS the default resolver will be used.
 */
@Component
@ConditionalOnProperty(prefix = "flais.operator", name = "resolve-crd-class-strategy", havingValue = "FLAIS", matchIfMissing = true)
public class FlaisResourceClassResolver implements ResourceClassResolver {

//    @Override
//    @SuppressWarnings("unchecked")
//    public <R extends CustomResource<?, ?>> Class<R> resolveCustomResourceClass(Reconciler<?> reconciler) {
//
//        final var type = ResolvableType.forClass(reconciler.getClass());
//
//        return (Class<R>) Arrays.stream(type.getSuperType().getInterfaces())
//                .filter(resolvableType -> resolvableType.toClass().getCanonicalName().equals(Reconciler.class.getCanonicalName()))
//                .findAny()
//                .orElseThrow(() -> new IllegalArgumentException(type.getType().getTypeName() + " does not implement Reconciler<T> interface"))
//                .resolveGeneric(0);
//    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends HasMetadata> Class<R> getResourceClass(Class<? extends Reconciler<R>> reconcilerClass) {
        final var type = ResolvableType.forClass(reconcilerClass);

        return (Class<R>) Arrays.stream(type.getSuperType().getInterfaces())
                .filter(resolvableType -> resolvableType.toClass().getCanonicalName().equals(Reconciler.class.getCanonicalName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(type.getType().getTypeName() + " does not implement Reconciler<T> interface"))
                .resolveGeneric(0);
    }
}
