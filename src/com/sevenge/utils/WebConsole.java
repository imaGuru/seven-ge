package com.sevenge.utils;

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
import com.sevenge.script.ScriptingEngine;

import fi.iki.elonen.NanoHTTPD;

/** Web server for serving a programming console created with NanoHTTPD **/
public class WebConsole extends NanoHTTPD {

	private ScriptingEngine scriptingEngine = null;
	Context context;
	private String commands[] = { "commands", "clear",
			"ls memorytype (internal, external, cache)",
			"delete memorytype (internal, external, cache) filename" };

	public WebConsole(Context context) {
		super(8080);
		this.context = context;
	}

	@Override
	public Response serve(IHTTPSession session) {
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
				msg += "<span style='color:green;'>" + "> " + command
						+ "</span>" + "<br>";
				msg += parseCommand(command);
			}

			return new NanoHTTPD.Response(msg);
		} else if ("script".equals(uri)) {

			String msg = "";

			String script = parms.get("value");

			if (scriptingEngine == null) {
				msg += "<span style='color:green;'>" + "> "
						+ "scripting engine not attached" + "</span>" + "<br>";
			} else {

				if (script != null) {
					this.scriptingEngine.executeScript(script);
					msg += "<span style='color:green;'>" + "> "
							+ "executed script" + "</span>" + "<br>";
				}
			}
			return new NanoHTTPD.Response(msg);
		}

		String msg = "<!DOCTYPE html>\r\n"
				+ "<html>\r\n"
				+ "<head>\r\n"
				+ "    <script>\r\n"
				+ "        window.onload = function() {\r\n"
				+ "            var form = document.getElementById('file-form');\r\n"
				+ "            var fileSelect = document.getElementById('file-select');\r\n"
				+ "            var uploadButton = document.getElementById('upload-button');\r\n"
				+ "            form.onsubmit = function(event) {\r\n"
				+ "                event.preventDefault();\r\n"
				+ "                uploadButton.innerHTML = 'Uploading...';\r\n"
				+ "                var files = fileSelect.files;\r\n"
				+ "                var formData = new FormData();\r\n"
				+ "                for (var i = 0; i < files.length; i++) {\r\n"
				+ "                    var file = files[i];\r\n"
				+ "                    formData.append('files[]', file, file.name);\r\n"
				+ "                }\r\n"
				+ "                var filename = \"\";\r\n"
				+ "                formData.append(name, file, filename);\r\n"
				+ "                var xhr = new XMLHttpRequest();\r\n"
				+ "                xhr.open('POST', '', true);\r\n"
				+ "                xhr.onload = function() {\r\n"
				+ "                    if (xhr.status === 200) {\r\n"
				+ "                        uploadButton.innerHTML = 'Upload';\r\n"
				+ "                    } else {\r\n"
				+ "                        alert('An error occurred!');\r\n"
				+ "                    }\r\n"
				+ "                };\r\n"
				+ "                xhr.send(formData);\r\n"
				+ "            }\r\n"
				+ "        };\r\n"
				+ "        function send_command(isScript) {\r\n"
				+ "            var xmlhttp;\r\n"
				+ "            xmlhttp = new XMLHttpRequest();\r\n"
				+ "            xmlhttp.onreadystatechange = function() {\r\n"
				+ "                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {\r\n"
				+ "                    if(xmlhttp.responseText != \"<span style='color:green;'>> clear</span><br>clear\"){\r\n"
				+ "                        var div = document.createElement(\"div\");\r\n"
				+ "                        div.innerHTML = xmlhttp.responseText;\r\n"
				+ "                        document.getElementById(\"command_results\").appendChild(div);\r\n"
				+ "                    }else{\r\n"
				+ "                        document.getElementById(\"command_results\").innerHTML = \"\";\r\n"
				+ "                    }\r\n"
				+ "                }\r\n"
				+ "            }\r\n"
				+ "            var newvalue = encodeURIComponent(document.getElementById(\"value\").value);\r\n"
				+ "            if(isScript){\r\n"
				+ "                xmlhttp.open(\"GET\", \"script?value=\" + newvalue, true);\r\n"
				+ "            }else {\r\n"
				+ "                xmlhttp.open(\"GET\", \"command?value=\" + newvalue, true);\r\n"
				+ "            }\r\n"
				+ "            xmlhttp.send();\r\n"
				+ "        }\r\n"
				+ "    </script>\r\n"
				+ "    <style>\r\n"
				+ "        body {\r\n"
				+ "            background-color: black;\r\n"
				+ "            color:white;\r\n"
				+ "            font-family: Tahoma, Arial;\r\n"
				+ "            background-image: url(\"http://s3.amazonaws.com/rapgenius/matrix.gif\");\r\n"
				+ "        }\r\n"
				+ "        h1 {\r\n"
				+ "            margin: 0;\r\n"
				+ "            margin-bottom: 5px;\r\n"
				+ "        }\r\n"
				+ "        textarea {\r\n"
				+ "            background-color: black;\r\n"
				+ "            color: white;\r\n"
				+ "            margin-top: 10px;\r\n"
				+ "            resize: none;\r\n"
				+ "        }\r\n"
				+ "        div#command_results {\r\n"
				+ "            overflow-y: auto;\r\n"
				+ "            height: 290px !important;\r\n"
				+ "            background-color: black;\r\n"
				+ "            padding: 5px;\r\n"
				+ "        }\r\n"
				+ "        div.buttons {\r\n"
				+ "            margin-top: 20px;\r\n"
				+ "        }\r\n"
				+ "        form {\r\n"
				+ "            display: inline-block;\r\n"
				+ "        }\r\n"
				+ "        input[type=\"button\"],\r\n"
				+ "        button[type=\"submit\"] {\r\n"
				+ "            background-color: black;\r\n"
				+ "            color: white;\r\n"
				+ "            display: inline-block;\r\n"
				+ "            padding: 10px;\r\n"
				+ "            border: 1px solid white;\r\n"
				+ "            text-transform: uppercase;\r\n"
				+ "        }\r\n"
				+ "        input[type=\"file\"] {\r\n"
				+ "            background-color: black;\r\n"
				+ "            color: white;\r\n"
				+ "            display: inline-block;\r\n"
				+ "            padding: 7px;\r\n"
				+ "            border: 1px solid white;\r\n"
				+ "            text-transform: uppercase;\r\n"
				+ "        }\r\n"
				+ "    </style>\r\n"
				+ "</head>\r\n"
				+ "<body>\r\n"
				+ "    <h1>SevenGE WebConsole</h1>\r\n"
				+ "    <div id=\"command_results\" style=\"overflow-y: scroll; height:300px;\">\r\n"
				+ "    </div>\r\n"
				+ "    <textarea tabindex=\"0\" autofocus id=\"value\" style=\"width:100%; height:200px;\"></textarea> <br>\r\n"
				+ "    <div class=\"buttons\">\r\n"
				+ "        <input type=\"button\" value=\"submit as command\" onclick=\"send_command(false)\">\r\n"
				+ "        <input type=\"button\" value=\"submit as script\" onclick=\"send_command(true)\">\r\n"
				+ "            <form id=\"file-form\" action=\"\" method=\"POST\">\r\n"
				+ "                <input type=\"file\" id=\"file-select\" name=\"files[]\" multiple=\"\">\r\n"
				+ "                <button type=\"submit\" id=\"upload-button\">Upload</button>\r\n"
				+ "            </form>\r\n" 
				+ "    </div>\r\n"
				+ "    \r\n" 
				+ "    </body>\r\n" 
				+ "    </html>";

		if (Method.POST.equals(method)) {

			try {
				Map<String, String> files = new HashMap<String, String>();
				try {
					session.parseBody(files);
				} catch (IOException e1) {
				}

				Set<String> keys = files.keySet();
				for (String key : keys) {
					String location = files.get(key);
					String filename = parms.get(key);

					try {
						File tempfile = new File(location);
						File newfile = IO.internal(filename);
						InputStream in = new FileInputStream(tempfile);
						OutputStream out = new FileOutputStream(newfile);
						IO.copyFile(in, out);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			} catch (ResponseException e) {
				e.printStackTrace();
			}
		}

		return new NanoHTTPD.Response(msg);

	}

	private String parseCommand(String command) {

		String response = "";
		String[] cs = command.split(" ");

		if (cs[0].equals("delete")) {

			if (cs[1].equals("internal")) {
				IO.deleteFile(IO.INTERNAL_PATH + "/" + cs[2]);
			} else if (cs[1].equals("external")) {
				IO.deleteFile(IO.EXTERNAL_PATH + "/" + cs[2]);
			} else if (cs[1].equals("cache")) {
				IO.deleteFile(IO.CACHE_PATH + "/" + cs[2]);
			}

		} else if (cs[0].equals("ls")) {

			if (cs[1].equals("internal")) {

				for (File file : IO.getFiles(IO.INTERNAL_PATH)) {
					response += file.getName() + " " + file.length() / 1024
							+ " kb" + "<br>";
				}

			} else if (cs[1].equals("external")) {

				for (File file : IO.getFiles(IO.EXTERNAL_PATH)) {
					response += file.getName() + " " + file.length() / 1024
							+ " kb" + "<br>";
				}

			} else if (cs[1].equals("cache")) {

				for (File file : IO.getFiles(IO.CACHE_PATH)) {
					response += file.getName() + " " + file.length() / 1024
							+ " kb" + "<br>";
				}

			}

		} else if (cs[0].equals("commands")) {
			for (String cmd : commands) {
				response += cmd + "<br>";
			}

		} else if (cs[0].equals("clear")) {
			response += "clear";
		}

		return response;
	}

	public void setScriptingEngine(ScriptingEngine scriptingEngine) {
		this.scriptingEngine = scriptingEngine;

	}
}
