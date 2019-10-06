import {PolymerElement, html} from '@polymer/polymer/polymer-element.js';       
class Scoreboard extends PolymerElement {
	static get is() {
		return 'liftingorder-template'
	}
       
	static get template() {
		return html`<style>
* {
	box-sizing: border-box;
}

:root {
  --narrow-width: 6%;
  --veryNarrow-width: 4%;
}

.wrapper {
	font-family: Arial, Helvetica, sans-serif;
	color: white;
	background-color: black;
	height: 100vh;
	padding: 2vmin 2vmin 2vmin 2vmin;
	overflow-y: hidden;
}

.attemptBar {
	display: flex;
	font-size: 3.6vmin;
	justify-content: space-between;
	width: 100%;
	height: 4vmin;
}

.attemptBar .startNumber {
	align-self: center;
}

.attemptBar .startNumber span {
	font-size: 70%;
	font-weight: bold;
	border-width: 0.2ex;
	border-style: solid;
	border-color: red;
	width: 1.5em;
	display: flex;
	justify-content: center;
	align-self: center;
}

.attemptBar .athleteInfo {
	display: flex;
	font-size: 3.6vmin;
	justify-content: space-between;
	align-items: baseline;
	width: 100%;
}

.athleteInfo .fullName {
	font-weight: bold;
	flex: 0 0 35%;
	text-align: left;
/* 	margin-left: 1em; */
	/*margin-right: auto;*/
	flex-grow: 0.5;
}

.athleteInfo .timer {
	flex: 0 0 15%;
	text-align: right;
	font-weight: bold;
	width: 10vw;
	display: flex;
	justify-content: flex-end;
}

.athleteInfo .decisionBox {
	position: fixed;
	top: 2vmin;
	right: 2vmin;
	width: 15vw;
	height: 10vh;
	background-color: black;
	display: none;
}

.athleteInfo .weight {
	color: aqua;
	display: flex;
	justify-content: center;
	align-items: baseline;
}

.group {
	font-size: 3vh;
	margin-top: 1vh;
	margin-bottom: 2vh;
}

table.results {
    table-layout: fixed;
    width: 100%;
	border-collapse: collapse;
	border: none;
	background-color: black;
	/*margin-bottom: 2vmin;*/
}

:host(.dark) table.results tr {
	background-color: black;
	color: white;
}

:host(.light) table.results tr {
	background-color: white;
	color: black;
}

th, td {
	border-collapse: collapse;
	border: solid 1px DarkGray;
	padding: 0.4vmin 1vmin 0.4vmin 1vmin;
	font-size: 2.1vh;
}

:host(.dark) th, td {
	font-weight: normal;
}

:host(.light) th, td {
	font-weight: bold;
}


.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media screen and (min-width: 1030px) {
	.showThRank {
		border-collapse: collapse;
		border: solid 1px DarkGray;
		border-left-style: none;
		padding: 0.5vmin 1vmin 0.5vmin 1vmin;
		font-size: 1.5vh;
		font-weight: normal;
		font-style: italic;
		width: 4vw;
		text-align: center;
	}
}

@media screen and (max-width: 1029px) {
	.showThRank {
		display: none;
		width: 0px;
		padding: 0 0 0 0;
		margin: 0 0 0 0;
	}
}

.thRank {
	border-collapse: collapse;
	border: solid 1px DarkGray;
	border-left-style: none;
	padding: 0.5vmin 1vmin 0.5vmin 1vmin;
	font-size: 1.5vh;
	font-weight: normal;
	font-style: italic;
	width: var(--veryNarrow-width);
	text-align: center;
}

.masters {
	display: table-cell;
	text-align: center;
	width: var(--veryNarrow-width);
}

.mastersHidden {
	display: none;
	width: 0px;
	padding: 0 0 0 0;
	margin: 0 0 0 0;
}

.narrow {
	width: var(--narrow-width);
	text-align: center;
}

@media screen and (min-width: 1020px) {
	.showRank {
		display: table-cell;
		width: var(--veryNarrow-width);
		text-align: center;
	}
}

@media screen and (max-width: 1029px) {
	.showRank {
		display: none;
		width: 0px;
		padding: 0 0 0 0;
		margin: 0 0 0 0;
	}
}

.veryNarrow {
	width: var(--veryNarrow-width);
	text-align: center;
}

.club {
	text-align: center;
}

.narrow {
	text-align: center;
}

:host(.dark) .good {
	background-color: green;
	font-weight: bold;
}

:host(.light) .good {
	background-color: green;
	font-weight: bold;
	color: white;
}

:host(.dark) .fail {
	background-color: red;
	font-weight: bold;
}

:host(.light) .fail {
	background-color: red;
	font-weight: bold;
	color: white;
}

:host(.dark)  .spacer {
	background-color: black;
}

:host(.light)  .spacer {
	background-color: gray;
}

.english {
	font-size: 85%;
}

:host(.dark) .request {
	background-color: black;
	font-style: italic;
}

:host(.light) .request {
	background-color: white;
	font-style: italic;
}

:host(.dark) .current {
	color: yellow;
	font-weight: bold;
}

:host(.light) .current {
	background-color: yellow;
	font-weight: bold;
}

.blink {
	animation: blink 1.5s step-start 0s infinite;
	-webkit-animation: blink 1.5s step-start 0s infinite;
}
@keyframes blink { 50% {opacity: 0.0;}}
@-webkit-keyframes blink { 50% {opacity: 0.0;}}

:host(.dark) .next {
	color: orange;
	font-weight: bold;
}

:host(.light) .next {
	background-color: gold;
	font-weight: bold;
}

:host(.dark) .empty {
	background-color: black;
	font-style: italic;
}

:host(.light) .empty {
	background-color: white;
	font-style: italic;
}

.breakTime {
	/* color: #99CCFF; */
	color: SkyBlue;
}

.athleteTimer {
	color: yellow;
}

.v-system-error {
	display: none;
}
</style>

<div class="wrapper" width="100%">
<div class="attemptBar" style$="[[_computeHidden(hidden)]]">
	<div class="athleteInfo" id="athleteInfoDiv" style$="[[_computeHidden(hidden)]]">
		<div class="fullName" id="fullNameDiv" inner-h-t-m-l="[[fullName]]"></div>
	</div>
</div>
<div class="group" id="groupDiv" style$="[[_computeHidden(hidden)]]">
	<span class="groupName">[[groupName]]</span> &ndash; [[liftsDone]]
</div>
<table width="100%" class="results" id="orderDiv" style$="[[_computeHidden(hidden)]]">
	<thead>
		<tr>
			<!--  [[t.x]] is loaded with the translation for key Scoreboard.x in the translation4.csv file -->
			<th width="5%" style="text-align: center;" inner-h-t-m-l="[[t.Start]]"></th>
			<th width="30%" inner-h-t-m-l="[[t.Name]]"></th>
			<th width="9%" class="narrow" inner-h-t-m-l="[[t.RequestedWeight]]"></th>
			<th width="9%" class="narrow" inner-h-t-m-l="[[t.NextAttempt]]"></th>
			<th width="9%" class$="[[_computeMasters(masters)]]" inner-h-t-m-l="[[t.AgeGroup]]"></th>
			<th width="9%" inner-h-t-m-l="[[t.Category]]"></th>
			<th width="20%" class='club' inner-h-t-m-l="[[t.Team]]"></th>
		</tr>
	</thead>
	<template is="dom-repeat" id="result-table" items="[[athletes]]" as="l">
		<template is="dom-if" if="[[l.isSpacer]]">
			<tr><td colspan="0" style="height:0.1ex; border:none"></td></tr>
		</template>
		<template is="dom-if" if="[[!l.isSpacer]]">
			<tr>
				<td width="5%" style="text-align: center;" class$="[[l.classname]]">[[l.startNumber]]</td>
				<td width="30%" class$="[[l.classname]] ellipsis">[[l.fullName]]</td>
				<td width="9%" class$="[[l.classname]]" style="text-align: center;">[[l.requestedWeight]]</td>
				<td width="9%" class$="[[l.classname]]" style="text-align: center;">[[l.nextAttemptNo]]</td>			
				<td width="9%" class$="[[_computeMasters(masters)]]">[[l.mastersAgeGroup]]</td>
				<td width="9%" style="text-align: center;" >[[l.category]]</td>
				<td width="20%" class="club ellipsis">[[l.teamName]]</td>	
			</tr>
		</template>
	</template>
</table>
</div>`;
	}

	ready() {
		super.ready();
		this.$.groupDiv.style.visibility="visible";
		this.$.fullNameDiv.style.display="block";
		this.$.orderDiv.style.visibility="visible"
	}

	start() {
		this.$.groupDiv.style.visibility="visible";
		this.$.fullNameDiv.style.display="block";
		this.$.orderDiv.style.visibility="visible"
	}

	reset() {
		console.debug("reset");
		this.$.groupDiv.style.visibility="visible";
		this.$.fullNameDiv.style.display="block";
		this.$.orderDiv.style.visibility="visible";
	}

	down() {
		console.debug("down");
	}

	doBreak() {
		console.debug("break");
		this.$.groupDiv.style.visibility="visible";
		this.$.fullNameDiv.style.display="block";
		this.$.orderDiv.style.display="block";
	}

	groupDone() {
		console.debug("done");
		this.$.groupDiv.style.visibility="hidden";
		this.$.fullNameDiv.style.display="block";
		this.$.orderDiv.style.visibility="hidden";
	}

	refereeDecision() {
		console.debug("refereeDecision");
	}

	_isEqualTo(title, string) {
		return title == string;
	}

	_computeHidden(hidden) {
		return hidden ? 'display:none' : 'display:block';
	}

	_computeMasters(masters) {
		return masters ? 'masters' : 'mastersHidden';
	}
}

customElements.define(Scoreboard.is, Scoreboard);
