
package fi.iki.elonen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.sevenge.IO;

/** An example of subclassing NanoHTTPD to make a custom HTTP server. */
public class HelloServer extends NanoHTTPD {

	Context context;

	public HelloServer (Context context) {
		super(8080);
		this.context = context;
	}

	@Override
	public Response serve (IHTTPSession session) {
		Method method = session.getMethod();
		String uri = session.getUri();
		Map<String, String> parms = session.getParms();
		if (uri.length() > 1) {
			// remove the starting /
			uri = uri.substring(1).toLowerCase();
		} else {
			uri = "";
		}

		if ("command".equals(uri)) {

			String msg = "";

			String command = parms.get("value");
			if (command != null) {
				msg += "> " + command;
				msg += parseCommand(command);
			}

			return new NanoHTTPD.Response(msg);
		}

		String msg = "<html>";

		msg += "<script>";
// msg += "function send_command()\r\n"
// + "{\r\n"
// + "var xmlhttp;\r\n"
// + "\r\n"
// + "xmlhttp = new XMLHttpRequest();\r\n"
// + " \r\n"
// + "xmlhttp.onreadystatechange = function(){\r\n"
// + "	if (xmlhttp.readyState==4 && xmlhttp.status==200){\r\n"
// +
// "		var command = document.getElementById(\"command_results\"); command.appendChild(document.createElement(\"br\")); command.appendChild(document.createTextNode(xmlhttp.responseText));\r\n"
// + "	}\r\n" + "}\r\n" + "xmlhttp.open(\"GET\",\"command\",true);\r\n" + "xmlhttp.send();\r\n" + "}";

		msg += "window.onload = function() {\r\n" + "\r\n" + "    var form = document.getElementById('file-form');\r\n"
			+ "    var fileSelect = document.getElementById('file-select');\r\n"
			+ "    var uploadButton = document.getElementById('upload-button');\r\n" + "\r\n"
			+ "    form.onsubmit = function(event) {\r\n" + "        event.preventDefault();\r\n" + "\r\n"
			+ "        // Update button text.\r\n" + "        uploadButton.innerHTML = 'Uploading...';\r\n" + "\r\n"
			+ "        // The rest of the code will go here...\r\n" + "\r\n"
			+ "        // Get the selected files from the input.\r\n" + "        var files = fileSelect.files;\r\n" + "\r\n"
			+ "        // Create a new FormData object.\r\n" + "        var formData = new FormData();\r\n" + "\r\n"
			+ "        // Loop through each of the selected files.\r\n" + "        for (var i = 0; i < files.length; i++) {\r\n"
			+ "            var file = files[i];\r\n" + "\r\n" + "            // Add the file to the request.\r\n"
			+ "            formData.append('files[]', file, file.name);\r\n" + "        }\r\n" + "\r\n"
			+ "        var filename = \"\";\r\n" + "\r\n" + "        // Files\r\n"
			+ "        formData.append(name, file, filename);\r\n" + "\r\n" + "        // Set up the request.\r\n"
			+ "        var xhr = new XMLHttpRequest();\r\n" + "\r\n" + "        // Open the connection.\r\n"
			+ "        xhr.open('POST', '', true);\r\n" + "\r\n" + "        // Set up a handler for when the request finishes.\r\n"
			+ "        xhr.onload = function() {\r\n" + "            if (xhr.status === 200) {\r\n"
			+ "                // File(s) uploaded.\r\n" + "                uploadButton.innerHTML = 'Upload';\r\n"
			+ "            } else {\r\n" + "                alert('An error occurred!');\r\n" + "            }\r\n"
			+ "        };\r\n" + "\r\n" + "        // Send the Data.\r\n" + "        xhr.send(formData);\r\n" + "\r\n" + "    }\r\n"
			+ "\r\n" + "};\r\n" + "\r\n" + "function send_command() {\r\n" + "    var xmlhttp;\r\n" + "\r\n"
			+ "    xmlhttp = new XMLHttpRequest();\r\n" + "\r\n" + "    xmlhttp.onreadystatechange = function() {\r\n"
			+ "        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {\r\n"
			+ "            var div = document.createElement(\"div\");\r\n" + "            div.innerHTML = xmlhttp.responseText;\r\n"
			+ "            document.getElementById(\"command_results\").appendChild(div);\r\n" + "        }\r\n" + "    }\r\n"
			+ "    var newvalue = encodeURIComponent(document.getElementById(\"value\").value);\r\n"
			+ "    xmlhttp.open(\"GET\", \"command?value=\" + newvalue, true);\r\n" + "    xmlhttp.send();\r\n" + "}";

// msg += "function send_command()\r\n" + "{\r\n" + "var xmlhttp;\r\n" + "\r\n" + "xmlhttp = new XMLHttpRequest();\r\n"
// + " \r\n" + "xmlhttp.onreadystatechange = function(){\r\n" + "	if (xmlhttp.readyState==4 && xmlhttp.status==200){\r\n"
// + "		var div = document.createElement(\"div\");\r\n" + "		div.innerHTML = xmlhttp.responseText;\r\n"
// + "		document.getElementById(\"command_results\").appendChild(div);\r\n" + "	}\r\n" + "}\r\n"
// + "var newvalue = encodeURIComponent(document.getElementById(\"value\").value);\r\n"
// + "xmlhttp.open(\"GET\",\"command?value=\"+newvalue,true);\r\n" + "xmlhttp.send();\r\n" + "}";
		msg += "</script>";
		msg += "<body><h1>Hello, this is the seven-ge server</h1>\n";

// msg += "<form action='' method='get'>\n" + "  <p>Command: <input type='text' name='command'></p><button>submit</button>\n"
// + "</form>\n";

		msg += "<input type='text' id='value'><input type='button' value='submit' onclick='send_command()'>";
		msg += "<div id='command_results'></div>";
		if (Method.GET.equals(method)) {

			String command = parms.get("command");
			if (command != null) {
				msg += "<p>Command requested : " + command + "</p>";
				msg += parseCommand(command);

			}

		}
		// POST
		else {

			try {
				Map<String, String> files = new HashMap<String, String>();
				session.parseBody(files);

				Set<String> keys = files.keySet();
				for (String key : keys) {
					String location = files.get(key);
					String filename = parms.get(key);
					File tempfile = new File(location);
					File newfile = IO.internal(filename);
					InputStream in = new FileInputStream(tempfile);
					OutputStream out = new FileOutputStream(newfile);
					IO.copyFile(in, out);

// for (File f : newfile.getParentFile().listFiles()) {
// DebugLog.d("HelloServer", f.getName());
// }
// DebugLog.d("HelloServer", newfile.getAbsoluteFile().toString());

				}

			} catch (ResponseException e) {
				System.out.println("i am error file upload post ");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("i am error file upload post ");
				e.printStackTrace();
			}
		}

		msg += "<form id=\"file-form\" action=\"\" method=\"POST\">\r\n"
			+ "  <input type=\"file\" id=\"file-select\" name=\"files[]\" multiple/>\r\n"
			+ "  <button type=\"submit\" id=\"upload-button\">Upload</button>\r\n" + "</form>";

		msg += "</body></html>\n";
		return new NanoHTTPD.Response(msg);

	}

	private String parseCommand (String command) {

		String response = "";
		String[] cs = command.split(" ");

		if (cs[0].equals("delete")) {

			if (cs[1].equals("internal")) {
				IO.deleteFile(IO.INTERNAL_PATH + "/" + cs[2]);
				for (File file : IO.getFiles(IO.INTERNAL_PATH)) {
					response += file.getName() + " " + file.length() / 1024 + " kb" + "<br>";
				}

			} else if (cs[1].equals("external")) {
				IO.deleteFile(IO.EXTERNAL_PATH + "/" + cs[2]);
				for (File file : IO.getFiles(IO.EXTERNAL_PATH)) {
					response += file.getName() + " " + file.length() / 1024 + " kb" + "<br>";
				}

			} else if (cs[1].equals("cache")) {
				IO.deleteFile(IO.CACHE_PATH + "/" + cs[2]);
				for (File file : IO.getFiles(IO.CACHE_PATH)) {
					response += file.getName() + " " + file.length() / 1024 + " kb" + "<br>";
				}

			}

		} else if (cs[0].equals("ls")) {

			if (cs[1].equals("internal")) {

				for (File file : IO.getFiles(IO.INTERNAL_PATH)) {
					response += file.getName() + " " + file.length() / 1024 + " kb" + "<br>";
				}

			} else if (cs[1].equals("external")) {

				for (File file : IO.getFiles(IO.EXTERNAL_PATH)) {
					response += file.getName() + " " + file.length() / 1024 + " kb" + "<br>";
				}

			} else if (cs[1].equals("cache")) {

				for (File file : IO.getFiles(IO.CACHE_PATH)) {
					response += file.getName() + " " + file.length() / 1024 + " kb" + "<br>";
				}

			}

		}
		return response;
	}
}
