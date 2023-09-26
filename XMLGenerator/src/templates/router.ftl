import {Switch, Route, Redirect} from "react-router-dom";
import Home from "./Home.jsx"
<#list classes as className, classProperties>
	<#if class??>
import ${className?cap_first}Create from ".${className?cap_first}Create"
import ${className?cap_first}Update from ".${className?cap_first}Update"
import ${className?cap_first}List from ".${className?cap_first}List"
	</#if>
</#list>

const Router = () => {
    return (
      <Switch>
        <Route exact path="/home" component={Home} />
        <#list classes as className, classProperties>
			<#if class??>
		<Route exact path="/${className?lower_case}/list" component={${className?cap_first}List} />
		<Route exact path="/${className?lower_case}/create" component={${className?cap_first}Create} />
		<Route exact path="/${className?lower_case}/update/:id" component={${className?cap_first}Update} />
			</#if>
		</#list>
        <Redirect to="/home" />
      </Switch>
    )
  };

export default Router;
