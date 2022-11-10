# FLAIS Operator Starter
[![CI](https://github.com/FINTLabs/flais-operator-starter/actions/workflows/ci.yaml/badge.svg)](https://github.com/FINTLabs/flais-operator-starter/actions/workflows/ci.yaml)
[![Javadoc](https://img.shields.io/badge/Javadoc-latest-brightgreen.svg?)](https://fintlabs.github.io/flais-operator-starter/)

A Spring Boot starter for FLAIS Java kubernetes operators

## Installation
1. Add repository
```groovy
repositories {
    ...
    repositories {
        maven {
            url "https://repo.fintlabs.no/releases"
        }
    }
}
```
2. Add library
```groovy
implementation 'no.fintlabs:flais-operator-starter:<latest version>'
```

## Usage

> See [here](https://github.com/FINTLabs/azurerator) for an example.

After adding the library to you project you need to implement the following classes:

### FlaisSpec
You need to implement this interface for each CRD you want to create. E.g.
```java
@Data
public class FileShareSpec implements FlaisSpec {
    private String name;
}
```

### FlaisCrd
You need to extend this class for each CRD you want to create. E.g.
```java
@Group("fintlabs.no")
@Version("v1alpha1")
@Kind("AzureFileShare")
public class FileShareCrd extends FlaisCrd<FileShareSpec> implements Namespaced {
    
    @Override
    protected FileShareSpec initSpec() {
        return new FileShareSpec();
    }
}
```

### FlaisWorkflow
````java
@Component
public class FileShareWorkflow extends FlaisWorkflow<FileShareCrd, FileShareSpec > {
}
````

### FlaisReconciler
````java
@Slf4j
@Component
@ControllerConfiguration(
        generationAwareEventProcessing = false
)
public class FileShareReconiler extends FlaisReconiler<FileShareCrd, FileShareSpec> {

    public FileShareReconiler(FileShareWorkflow workflow,
                              List<? extends DependentResource<?, FileShareCrd>> eventSourceProviders,
                              List<? extends Deleter<FileShareCrd>> deleters) {
        super(workflow, eventSourceProviders, deleters);
    }
}
````

### Dependent resources of your choice

#### FlaisExternalDependentResource
````java
@Component
public class FileShareDependentResource extends FlaisExternalDependentResource<FileShare, FileShareCrd, FileShareSpec> {


    private final FileShareService fileShareService;

    public FileShareDependentResource(FileShareWorkflow workflow,
                                      FileShareService fileShareService,
                                      SomeOtherDR someOtherDR) {
        super(FileShare.class, workflow);
        this.fileShareService = fileShareService;
        setPollingPeriod(Duration.ofMinutes(10).toMillis());
        dependsOn(someOtherDR);
    }


    @Override
    protected FileShare desired(FileShareCrd primary, Context<FileShareCrd> context) {
        ...
    }

    @Override
    public void delete(FileShareCrd primary, Context<FileShareCrd> context) {
        ...
    }

    @Override
    public FileShare create(FileShare desired, FileShareCrd primary, Context<FileShareCrd> context) {
        ...
    }

    @Override
    public Set<FileShare> fetchResources(FileShareCrd primaryResource) {
        ...
    }

}
````

#### FlaisKubernetesDependentResource

````java
@Component
@KubernetesDependent(labelSelector = "app.kubernetes.io/managed-by=flaiserator")
public class FileShareSecretDependentResource extends FlaisKubernetesDependentResource<Secret, FileShareCrd, FileShareSpec> {

    public FileShareSecretDependentResource(FlaisWorkflow<FileShareCrd, FileShareSpec> workflow, FileShareDependentResource fileShareDependentResource, KubernetesClient kubernetesClient) {

        super(Secret.class, workflow, kubernetesClient);
        dependsOn(fileShareDependentResource);
    }

    @Override
    protected Secret desired(FileShareCrd resource, Context<FileShareCrd> context) {

        log.debug("Desired secret for {}", resource.getMetadata().getName());

        Optional<FileShare> fileShare = context.getSecondaryResource(FileShare.class);
        FileShare azureFileShare = fileShare.orElseThrow();

        HashMap<String, String> labels = new HashMap<>(resource.getMetadata().getLabels());

        labels.put("app.kubernetes.io/managed-by", "flaiserator");
        return new SecretBuilder().withNewMetadata().withName(resource.getMetadata().getName()).withNamespace(resource.getMetadata().getNamespace()).withLabels(labels).endMetadata().withStringData(new HashMap<>() {{
            put("fint.azure.storage-account.connection-string", azureFileShare.getConnectionString());
            put("fint.azure.storage-account.file-share.name", azureFileShare.getShareName());
        }}).build();
    }
}
````

