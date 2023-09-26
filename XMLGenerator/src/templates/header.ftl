import { useState } from "react";
import { useHistory } from "react-router-dom";

import ".styles.css";

const REGEX = /[A-Z][a-z]+/g;

function Header() {
  const history = useHistory();
  const [menuVisible, setMenuVisible] = useState(false);
  const normalizeWord = (word) => word?.match(REGEX)?.join(" ") || word;

  const handleItemClick = (path) => {
    history.push(path)
    setMenuVisible(false);
  };

  return (
    <>
      <div className="header">
        <div className="menu" onClick={() => setMenuVisible(!menuVisible)}>
          <div className="menu-item">Menu items</div>
        </div>
      </div>
      {menuVisible && (
        <div className="Dropdown-header">
        <div className="menu-dropdown">
        <#list classes as class>
			<#if class??>
		  <div className="menu-dropdown-item" onClick={() => handleItemClick("/${class?lower_case}/list")}>{normalizeWord("${class?cap_first}")}</div>
			</#if>
		</#list>
        </div>
      </div>
      )}
    </>
  );
}

export default Header;
