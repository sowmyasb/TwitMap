<html>
  <body>
    <script>
      function setupEventSource() {
        var output = document.getElementById("output");
        if (typeof(EventSource) !== "undefined") {
          var msg = "Get Full Data";
          var source = new EventSource("twitmapsse?msg=" + msg);
          source.onmessage = function(event) {
            output.innerHTML += event.data + "<br>";
          };
          source.addEventListener('close', function(event) {
            output.innerHTML += event.data + "<hr/>";
            source.close();
            }, false);
        } else {
          output.innerHTML = "Sorry, Server-Sent Events are not supported in your browser";
        }
        return false;
      }
      window.onload = setupEventSource();
    </script>
    <h2>Location Demo</h2>
    <div id="output"></div>
  </body> 
</html>
