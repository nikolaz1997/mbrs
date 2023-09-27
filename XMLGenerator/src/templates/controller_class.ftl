@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/${className?lower_case}s")
public class ${className}Controller {

    // TODO: Adjust method annotations and add more controller functions that are required by your logic.

    private final ${className}Service ${className?lower_case}Service;

    public ${className}Controller(${className}Service ${className?lower_case}Service) {
        this.${className?lower_case}Service = ${className?lower_case}Service;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<${className}DTO> get${className}s() {
        // TODO: Implement logic that is convenient for your controller method

        return null;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ${className}DTO create${className}() {
        // TODO: Implement logic that is convenient for your controller method

        return null;
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ${className}DTO update${className}() {
        // TODO: Implement logic that is convenient for your controller method

        return null;
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete${className}() {
        // TODO: Implement logic that is convenient for your controller method
    }
}