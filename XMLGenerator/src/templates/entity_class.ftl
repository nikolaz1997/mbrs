@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name="${tableName}")
public class ${className}Entity {

<#list classFields as classField>
    ${classField}
</#list>
}