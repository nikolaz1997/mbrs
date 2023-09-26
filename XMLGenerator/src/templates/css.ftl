.main-container {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 90vh;
	width: 100vw;
	background: linear-gradient(to right, #ffcc00 0%, #ff33cc 100%);
}

.Form {
	height: auto;
	width: 400px;
	padding: 20px;
	display: flex;
	flex-direction: column;
	background-color: #424853;
	border-radius: 8px;
	margin-top: 20px;
	margin-bottom: 20px;
}

.Label {
	font-size: 16px;
	color: white;
	margin-bottom: 5px;
	margin-top: 5px;
}

.Input {
	border-radius: 8px;
	height: 25px;
	margin-bottom: 5px;
	border-style: solid;
	border-color: white;
	border-width: 1;
	outline: none;
}

.Input:focus {
	border-color: #4af0b9;
}

.Button {
	width: 200px;
	height: 30px;
	border-radius: 8px;
	font-size: 16px;
	margin-top: 20px;
	border-style: none;
	background-color: #4af0b9;
	color: #282c34;
	align-self: center;
	text-align: center;
	font-weight: bold;
	cursor: pointer;
}

.Multiselect {
	padding: 7px;
	background-color: white;
	border-radius: 8px;
	margin-bottom: 5px;
	max-height: 100px;
	overflow-y: scroll;
}

.Multiselect-item {
	padding: 3px;
	padding-bottom: 3px;
	margin-top: 3px;
	font-size: 14px;
	color: black;
	cursor: pointer;
}

.selected {
	background-color: #4af0b9;
}

.header {
	background-color: #424853;
	height: 10vh;
	width: 100vw;
	display: flex;
	justify-content: column;
	justify-content: flex-end;
	align-items: center;
}

.menu {
	height: 10vh;
	width: 150px;
	display: flex;
	justify-content: center;
	align-items: center;
	cursor: pointer;
	font-size: 20px;
	color: white;
	font-weight: bold;
}

.menu-dropdown {
	background-color: #242527;
	width: 250px;
	align-self: flex-end;
	min-height: 50px;
	max-height: 270px;
	overflow-y: scroll;
	position: absolute;
	z-index: 1;
	right: 10px;
}

.menu-dropdown::-webkit-scrollbar {
	display: none;
}

.menu-dropdown {
	-ms-overflow-style: none; /* IE and Edge */
	scrollbar-width: none; /* Firefox */
}

.menu-dropdown-item {
	background-color: #242527;
	width: 230px;
	display: flex;
	height: 30px;
	align-items: center;
	margin-left: 10px;
	border-bottom-color: rgb(133, 133, 133);
	border-bottom-style: solid;
	border-bottom-width: 1px;
	color: white;
	margin-top: 10px;
	margin-bottom: 10px;
	cursor: pointer;
}

.menu-dropdown-item:hover {
	background-color: #4af0b9;
	color: black;
	font-weight: 600;
}

.content-wrapper {
	width: 50vw;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	text-align: center;
	background-color: white;
	border-radius: 8px;
	padding: 50px;
	margin-top: 30px;
	margin-bottom: 30px;
}

.Table-wrapper {
	width: 85vw;
	padding: 15px;
	background-color: #424853;
	color: white;
}

.Table-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	flex-direction: row;
	width: 98%;
	height: 70px;
}

.Table-heading {
	font-size: 25px;
	font-weight: bold;
}

table {
	border-spacing: 0;
	width: 100%;
}

th,
td {
	text-align: left;
	margin: 16px;
	padding-top: 16px;
	padding-bottom: 16px;
}

tr:nth-child(even) {
	background-color: rgb(92, 94, 104);
}

h1 {
	font-size: 32px;
	font-weight: 700;
	color: black;
}

p {
	font-size: 20px;
	color: rgb(44, 44, 44);
}

.Table-button {
	width: 90px;
	height: 30px;
	border-radius: 4px;
	font-size: 14px;
	border-style: none;
	background-color: #4af0b9;
	color: #282c34;
	text-align: center;
	font-weight: bold;
	cursor: pointer;
	display: flex;
	justify-content: center;
	align-items: center;
}

.delete {
	background-color: rgb(151, 0, 45);
	color: white;
}

h2 {
	color: white;
}

