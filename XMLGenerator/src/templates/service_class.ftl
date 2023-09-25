@Service
@RequiredArgsConstructor
public class ${className}Service {

    private final ${className}Repository ${className?lower_case}Repository;

    public ${className}Service(${className}Repository ${className?lower_case}Repository) {
        this.${className?lower_case}Repository = ${className?lower_case}Repository;
    }

    // TODO: Add more service functions that are required by your logic
}