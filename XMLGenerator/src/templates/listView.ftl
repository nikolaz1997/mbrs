import { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import axios from 'axios';

const api = 'http://localhost:8080/';

import "./styles.css"

<#if className??>const ${className?cap_first}FormList = () => {
  const history = useHistory();

  const [${className?lower_case}s, set${className?cap_first}s] = useState([]);
    <#list properties as property>
        <#if property?? && !property.jsonIgnore??>
            <#if property.name?cap_first != "Id">
                <#if  property.type == "String">
  const [${property.name}Search, set${property.name?cap_first}Search] = useState("");
                <#elseif property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long">
  const [${property.name}Search, set${property.name?cap_first}Search] = useState("");
                </#if>
            </#if>
        </#if>
    </#list>

  const capitalizeFirstLetter = (word) => word.charAt(0).toUpperCase() + word.slice(1);
  
  const normalizeWord = (word) => capitalizeFirstLetter(word)?.match(/[A-Z][a-z]+/g)?.join(" ") || word;

  const genProperties = (${className}) => {
    const properties = [];
    Object.keys(${className}).forEach((k, i) => {
      if (!Array.isArray(${className}[k])) {
        const isObject = typeof ${className}[k] === 'object';
        properties.push({
        	property: !isObject ? normalizeWord(k) : normalizeWord(k),
        	value: isObject ? ${className}[k]?.id : ${className}[k],
        })
      }
    })
    return properties;
  }

  useEffect(() => {
    const fetchData = async () => {
      let ${className?lower_case}s = (await axios.get(`${r"${api}"}${className?lower_case}/all`)).data;
      set${className?cap_first}s(${className?lower_case}s);
    }
    fetchData();
  }, []);

  const delete${className?cap_first} = async (id) => {
    await axios.delete(`${r"${api}"}${className?lower_case}/delete/${r"${id}"}`).then(set${className?cap_first}s(${className?lower_case}s.filter((p) => p.id !== id)));
  }

  return (
    <div className="main-container">
      <div className="content-wrapper table-wrapper">
          <div className="table-header">
            <div className="table-heading">All ${className?cap_first}s</div>
            <div className="menu" onClick={() => history.push("/${className?lower_case}/create")}>+ Add ${className?lower_case}</div>
          </div>

        <div>
        <#list properties as property>
            <#if property?? && !property.jsonIgnore??>
                <#if property.name?cap_first != "Id">
                    <#if property.type == "String">
            <div>
                {normalizeWord("${property.name}")}: <input type="text" onChange={e => set${property.name?cap_first}Search(e.target.value)} />
            </div>
                    <#elseif property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long">
            <div>
                {normalizeWord("${property.name}")}: <input type="number" onChange={e => set${property.name?cap_first}Search(e.target.value)} />
            </div>
                    </#if>
                </#if>
            </#if>
        </#list>

        </div>
        
        <table>
            <tbody>
                <tr>
                {${className?lower_case}s.length > 0 && genProperties(${className?lower_case}s[0]).map((h) => !Array.isArray(h.value) ? (
                    <th key={${r"`${"}${className?lower_case}s${r"[0].id} ${h.property}`"}}><div>{h.property}</div></th>
                ) : null)}
                </tr>
                {${className?lower_case}s.map((a) => {
                    if (<#list properties as property><#if property?? && !property.jsonIgnore??><#if property.name?cap_first != "Id"><#if property.type == "String">a?.${property.name}.includes(${property.name}Search) && <#elseif property.type == "Double" || property.type == "double" || property.type == "float" || property.type == "Integer"  || property.type == "int" || property.type == "Long"  || property.type == "long">a?.${property.name}.toString().includes(${property.name}Search.toString()) && </#if></#if></#if></#list>true) {
                        return (
                        <tr key={${r"`${a.id}tr`"}}>
                            {genProperties(a).map((p, i) => !Array.isArray(p.value) ? (
                            <td key={`${r"${a.id}"} ${r"${p.value}"} ${r"${i}"}`}>{p.value}</td>
                                ) : null)}
                            <td>
                                <div className="table-button" onClick={() => history.push(`/${className?lower_case}/update/${r"${a.id}"}`)}>
                                    More details
                                </div>
                            </td>
                            <td>
                                <div className="table-button delete" onClick={() => delete${className?cap_first}(a.id)}>
                                    Delete
                                </div>
                            </td>
                        </tr>
                        );
                    }
                })}
            </tbody>
        </table>
      </div>
    </div>
  );
}

export default ${className?cap_first}FormList;

</#if>
