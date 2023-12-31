import {Switch, Route, Redirect} from "react-router-dom";
import Home from "./Home.jsx"

<#list classes as class>
	<#if class??>
import ${class?cap_first}Create from "./${class?cap_first}Create.jsx"
import ${class?cap_first}Update from "./${class?cap_first}Update.jsx"
import ${class?cap_first}ListView from "./${class?cap_first}ListView.jsx"
	</#if>
</#list>

const Router = () => {
    return (
      <Switch>
        <Route exact path="/home" component={Home} />
        <#list classes as class>
			<#if class??>
		<Route exact path="/${class?lower_case}/list" component={${class?cap_first}ListView} />
		<Route exact path="/${class?lower_case}/create" component={${class?cap_first}Create} />
		<Route exact path="/${class?lower_case}/update/:id" component={${class?cap_first}Update} />
			</#if>
		</#list>
        <Redirect to="/home" />
      </Switch>
    )
  };

export default Router;
