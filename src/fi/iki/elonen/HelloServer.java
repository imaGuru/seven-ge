
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

		String msg = "<html><body><h1>Hello, this is the seven-ge server</h1>\n";
		Map<String, String> parms = session.getParms();

		msg += "<form action='?' method='get'>\n" + "  <p>Command: <input type='text' name='command'></p><button>submit</button>\n"
			+ "</form>\n";

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

		msg += "<html><body><form action='?' method='post' enctype='multipart/form-data'>"
			+ "<input type='file' name='file' multiple/><br /><input type='submit'name='submit' "
			+ "value='Upload'/></form></body></html>";

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
