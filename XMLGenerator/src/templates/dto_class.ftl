@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ${className}DTO {

<#list classFields as classField>
    ${classField};
</#list>
}