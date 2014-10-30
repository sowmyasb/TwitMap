<!DOCTYPE html>
<html>
<head>
<style type="text/css">
#map-canvas {
	height: 200px;
	margin: 10;
	padding: 0;
}
</style>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDTHW-i7oTS_w1qPUCcXIGdi9LrI4V4cpo">
	
</script>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<body>
<script type="text/javascript" language="javascript">
	var wsUri = "ws://localhost:8887/";

	function init() {
		output = document.getElementById("output");
	}
	function send_message() {
		websocket = new WebSocket(wsUri);
		websocket.onopen = function(evt) {
			onOpen(evt)
		};
		websocket.onmessage = function(evt) {
			onMessage(evt)
		};
		websocket.onerror = function(evt) {
			onError(evt)
		};
	}
	function onOpen(evt) {
		writeToScreen("Connected to Endpoint!");
		doSend(textID.value);
	}
	function onMessage(evt) {
		writeToScreen("Message Received: " + evt.data);
	}
	function onError(evt) {
		writeToScreen('ERROR: ' + evt.data);
	}
	function doSend(message) {
		writeToScreen("Message Sent: " + message);
		websocket.send(message);
		//websocket.close();
	}
	function writeToScreen(message) {
		var pre = document.createElement("p");
		pre.style.wordWrap = "break-word";
		pre.innerHTML = message;

		output.appendChild(pre);
	}

	function WebSocketTest() {
		if ("WebSocket" in window) {
			alert("WebSocket is supported by your Browser!");
			// Let us open a web socket
			var ws = new WebSocket("ws://localhost:9998/echo");
			ws.onopen = function() {
				// Web Socket is connected, send data using send()
				ws.send("Message to send");
				alert("Message is sent...");
			};
			ws.onmessage = function(evt) {
				var received_msg = evt.data;
				alert("Message is received...");
			};
			ws.onclose = function() {
				// websocket is closed.
				alert("Connection is closed...");
			};
		} else {
			// The browser doesn't support WebSocket
			alert("WebSocket NOT supported by your Browser!");
		}
	}
	function initialize() {
		var myLatlng = new google.maps.LatLng(-25.363882, 131.044922);
		var taxiData = [ new google.maps.LatLng(37.782551, -122.445368),
				new google.maps.LatLng(37.782745, -122.444586),
				new google.maps.LatLng(37.782842, -122.443688),
				new google.maps.LatLng(37.782919, -122.442815),
				new google.maps.LatLng(37.782992, -122.442112),
				new google.maps.LatLng(37.783100, -122.441461),
				new google.maps.LatLng(37.783206, -122.440829),
				new google.maps.LatLng(37.783273, -122.440324),
				new google.maps.LatLng(37.783316, -122.440023),
				new google.maps.LatLng(37.783357, -122.439794),
				new google.maps.LatLng(37.783371, -122.439687) ];

		var mapOptions = {
			center : myLatlng,
			zoom : 3
		};
		var map = new google.maps.Map(document.getElementById('map-canvas'),
				mapOptions);

		var addresses = [ 'Norway', 'Africa', 'Asia', 'North America',
				'South America' ];

		//for (var x = 0; x < addresses.length; x++) {
		//  $.getJSON('http://maps.googleapis.com/maps/api/geocode/json?address='+addresses[x]+'&sensor=false', null, function (data) {
		//var p = data.results[0].geometry.location
		// var latlng = myLatlng
		/* var marker =   new google.maps.Marker({
		    position: latlng,
		    map: map
		}); */

		//}); 
		//} 
		for (var x = 0; x < addresses.length; x++) {
			$.getJSON(
					'http://maps.googleapis.com/maps/api/geocode/json?address='
							+ addresses[x] + '&sensor=false', null, function(
							data) {
						var p = data.results[0].geometry.location
						var latlng = new google.maps.LatLng(p.lat, p.lng);
						new google.maps.Marker({
							position : latlng,
							map : map
						});
					});
		}
		var marker = new google.maps.Marker({
			position : myLatlng,
			map : map,
			title : 'Hello World!'
		});
	}
	google.maps.event.addDomListener(window, 'load', initialize);
</script>
<h2 style="text-align: center;">
	Hello World WebSocket Client
	</h2>
	<br>
	<div style="text-align: center;">
		<form action="">
			<input onclick="send_message()" value="Send" type="button"> <input
				id="textID" name="message" value="Hello WebSocket!" type="text"><br>
		</form>
	</div>
	<div id="output"></div>
	</body>
</html>

<!-- </head>
<body>
	<div id="map-canvas"></div>
</body>
</html> -->
<%-- <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.amazonaws.*" %>
<%@ page import="com.amazonaws.auth.*" %>
<%@ page import="com.amazonaws.services.ec2.*" %>
<%@ page import="com.amazonaws.services.ec2.model.*" %>
<%@ page import="com.amazonaws.services.s3.*" %>
<%@ page import="com.amazonaws.services.s3.model.*" %>
<%@ page import="com.amazonaws.services.dynamodbv2.*" %>
<%@ page import="com.amazonaws.services.dynamodbv2.model.*" %>

<%! // Share the client objects across threads to
    // avoid creating new clients for each web request
    private AmazonEC2         ec2;
    private AmazonS3           s3;
    private AmazonDynamoDB dynamo;
 %>

<%
    /*
     * AWS Elastic Beanstalk checks your application's health by periodically
     * sending an HTTP HEAD request to a resource in your application. By
     * default, this is the root or default resource in your application,
     * but can be configured for each environment.
     *
     * Here, we report success as long as the app server is up, but skip
     * generating the whole page since this is a HEAD request only. You
     * can employ more sophisticated health checks in your application.
     */
    if (request.getMethod().equals("HEAD")) return;
%>

<%
    if (ec2 == null) {
        AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
        ec2    = new AmazonEC2Client(credentialsProvider);
        s3     = new AmazonS3Client(credentialsProvider);
        dynamo = new AmazonDynamoDBClient(credentialsProvider);
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <title>Hello AWS Web World!</title>
    <link rel="stylesheet" href="styles/styles.css" type="text/css" media="screen">
</head>
<body>
    <div id="content" class="container">
        <div class="section grid grid5 s3">
            <h2>Amazon S3 Buckets:</h2>
            <ul>
            <% for (Bucket bucket : s3.listBuckets()) { %>
               <li> <%= bucket.getName() %> </li>
            <% } %>
            </ul>
        </div>

        <div class="section grid grid5 sdb">
            <h2>Amazon DynamoDB Tables:</h2>
            <ul>
            <% for (String tableName : dynamo.listTables().getTableNames()) { %>
               <li> <%= tableName %></li>
            <% } %>
            </ul>
        </div>

        <div class="section grid grid5 gridlast ec2">
            <h2>Amazon EC2 Instances:</h2>
            <ul>
            <% for (Reservation reservation : ec2.describeInstances().getReservations()) { %>
                <% for (Instance instance : reservation.getInstances()) { %>
                   <li> <%= instance.getInstanceId() %></li>
                <% } %>
            <% } %>
            </ul>
        </div>
    </div>
</body>
</html> --%>