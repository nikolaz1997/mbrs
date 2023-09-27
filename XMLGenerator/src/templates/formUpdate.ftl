import { useState, useEffect } from "react";
import { useParams, useHistory } from 'react-router-dom'
import axios from 'axios';

import "./styles.css";

const api = 'http://localhost:8080/';

<#if className??>const ${className?cap_first}FormUpdate = () => {
  const history = useHistory();
  const { id } = useParams();

	<#list properties as property>
		<#if property??>
		<#if property.name?cap_first != "Id" && !property.jsonIgnore??>
			<#if property.type == "String">
  const [${property.name}, set${property.name?cap_first}] = useState("");
            <#elseif property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long">
  const [${property.name}, set${property.name?cap_first}] = useState(0);
  			<#elseif property.type == "Date" ||  property.type == "LocalDate">
  const [${property.name}, set${property.name?cap_first}] = useState(new Date());
			</#if>
		</#if>
		</#if>
	</#list>
	
	<#list properties as property>
		<#if property?? && !property.jsonIgnore??>
		<#if property.name?cap_first != "Id" && property.type != "Double" && property.type != "double" && property.type != "float" && property.type != "Integer"  && property.type != "int" && property.type != "Long"  && property.type != "long" && property.type != "String" && property.type != "Date" &&  property.type != "LocalDate">
		</#if>
		</#if>
	</#list>

	const props = [<#list properties as property><#if property?? && !property.jsonIgnore??><#if property.name?cap_first != "Id"><#if property.type == "String" || property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long" || property.type == "Date" ||  property.type == "LocalDate">${property.name}</#if>, </#if></#if></#list>]
  
    <#list properties as property>
		<#if property?? && !property.jsonIgnore??>
		<#if property.name?cap_first != "Id" && property.type != "Double" && property.type != "double" && property.type != "float" && property.type != "Integer"  && property.type != "int" && property.type != "Long"  && property.type != "long" && property.type != "String" && property.type != "Date" &&  property.type != "LocalDate">
		</#if>
		</#if>
	</#list>

	const load${className?cap_first} = async () => {
      let ${className?lower_case} = (await axios.get(`${r"${api}"}${className?lower_case}/get/${r"${id}"}`)).data;
      if (${className?lower_case}) {
    <#list properties as property>
		<#if property?? && !property.jsonIgnore??>
		<#if property.name?cap_first != "Id">
		</#if>
		</#if>
	</#list>
	  }
    };
  
	<#list properties as property>
		<#if property?? && !property.jsonIgnore??>
		<#if property.name?cap_first != "Id">
			<#if  property.type == "String" || property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long" || property.type == "Date" ||  property.type == "LocalDate">
  const handle${property.name?cap_first}Change = (event) => {
	set${property.name?cap_first}(event.target.value);
  };
  			</#if>
  		</#if>
		</#if>
	</#list>
		<#list properties as property>
		<#if property?? && !property.jsonIgnore??>
		</#if>
	</#list>
	
  const normalizeWord = (word) => word?.match(/[A-Z][a-z]+/g)?.join(" ") || word;

  const everythingOkay = () => {
    for (const prop of props) {
      if((typeof prop === "string" && prop === '') || (typeof prop === "number" && prop === 0) || (Array.isArray(prop) && prop.length === 0) || prop == null)
        return false;
    }

    return true;
  }
  
  const submit = async (event) => {
    event.preventDefault();
    const request = {
      	<#list properties as property>
		<#if property?? && !property.jsonIgnore??>
		<#if property.name?cap_first != "Id">
			<#if  property.type == "String" || property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long" || property.type == "Date" ||  property.type == "LocalDate">
  		${property.name?lower_case}: ${property.name},
  			</#if>
  		</#if>
		</#if>
	</#list>
	}
    await axios.put(`${r"${api}"}${className?lower_case}/update/${r"${id}"}`, request);
    history.push("/${className?lower_case}/list");
  }

return (
    <div className="main-container">
      <form className="form" onSubmit={submit}>
        <h2>Update {normalizeWord("${className}")}</h2>
        <#list properties as property>
          <#if property?? && !property.jsonIgnore??>
          <#if property.name?cap_first != "Id">
            <label className="label">{normalizeWord("${property.name?cap_first}")}:</label>
            <#if property.type == "String">
            <input type="text" value={${property.name}} onChange={handle${property.name?cap_first}Change} className="input" />
            <#elseif property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int">
            <input type="number" value={${property.name}} onChange={handle${property.name?cap_first}Change} className="input" />
            </#if>
          </#if>
          </#if>
		</#list>
        <input type="submit" value="Update" className="button" disabled={!everythingOkay()} />
       </form>
    </div>
  );
}

export default ${className?cap_first}FormUpdate;

</#if>
