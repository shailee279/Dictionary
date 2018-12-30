import java.io.*;
import java.net.*;

import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;


import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/*
 * performs server function
 */
public class MultiThreadServer extends Thread
{

	private static final String ADD = "Add";
	private static final String DELETE = "Delete";
	private static final String EXIT = "Exit";
	private File fileName;
	private Socket clientSocket;


	public MultiThreadServer(Socket clientSocket, String fileName)
	{
		this.clientSocket = clientSocket;
		this.fileName = new File(fileName);
	}

	@Override
	public void run()
	{
		String word;
		String value;
		String clientMessage;
		String command;
		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			while ((clientMessage = in.readLine())!= null)
			{
				while (!(clientMessage).equals(EXIT)) 
				{
					StringTokenizer commandSet = new StringTokenizer(clientMessage, "-");
					command = commandSet.nextToken();
					//System.out.println("operation"+command);
					if(command.equals("Search")) {					
						JSONObject data1 = readDictionary();				
						word = commandSet.nextToken();
						searchWord(word, data1, out);
						
					}
					else if (command.equals(DELETE))
					{
						JSONObject data3 = readDictionary();
						word = commandSet.nextToken();
						value = commandSet.nextToken();
						deleteWord(word, data3, out);
					}
					else if(command.equals(ADD)) 
					{
						JSONObject data2 = readDictionary();
						word = commandSet.nextToken();
						value = commandSet.nextToken();
						addWord(word,value,data2,out);
						

					}

					break;
				}
			}

		}
		catch(FileNotFoundException e) 
		{
			System.out.println("File does not exist");
		}
		catch(SocketException e)
		{
			System.out.println("Problem while creating socket" + "\n");
		}
		catch(JSONException e)
		{
			System.out.println("Cannot read dictionary." + "\n");
		}
		catch(IOException e)
		{
			System.out.println("Connection error"+ "\n");
			//Server.serverWindow.append("Error during data transfer." + "\n");
		}
		finally
		{
			if (clientSocket != null) {
				try {
					clientSocket.close();
					System.out.println("Client disconnected "+ "\n");
					//Server.serverWindow.append("Client disconnected. " + "\n");
				}
				catch (IOException e) {
					System.out.println("Problem in connection"+ "\n");
					//Server.serverWindow.append("problem while creating/closing socket." + "\n");
				}
			}
		}
	}




	@SuppressWarnings("unchecked")
	private synchronized String addWord(String term, String confirm, JSONObject dictionary, BufferedWriter output) throws IOException {
		String val = "";
		try {
			if (dictionary.containsKey(term.toLowerCase()))
			{
				String message = "Word already present."+"\n";
				output.write(message);
				output.flush();
				writeToDict(dictionary);
				return message;
			}
			else
			{
				JSONArray newWordDefinition = new JSONArray();
				newWordDefinition.add(confirm.toLowerCase());
				dictionary.put(term.toLowerCase(), newWordDefinition);
				output.write("word added" + "\n");
				output.flush();
				writeToDict(dictionary);
				return val;
			}
		}
		catch (IOException e) {
			System.out.println("Error while writing to file." + "\n");
			return val;
		}
	}
	private String searchWord(String term, JSONObject dictionary, BufferedWriter output)
			throws IOException {
		String returnValue="";
		if (dictionary.containsKey(term.toLowerCase()))
		{
			JSONArray t;
			t = (JSONArray) dictionary.get(term.toLowerCase());

			returnValue = (String) t.get(0);
			output.write(returnValue + "\n");			
			output.flush();
			return returnValue;
		}

		else {

			output.write("\""+term+"\" is not present." + "\n");
			output.flush();
			return returnValue;
		}
	}


	private void writeToDict(JSONObject dictionary) {
		FileWriter outputStream = null;
		try {
			outputStream = new FileWriter(fileName, false);
			outputStream.write(dictionary.toString());
			outputStream.flush();
			outputStream.close();

		}
		catch (IOException e) {
			System.out.println("Error while writing to file." + "\n");
		}
	}



	private JSONObject readDictionary() throws JSONException ,FileNotFoundException {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new FileReader(fileName));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return (JSONObject) obj;
	}
	private synchronized void deleteWord(String word, JSONObject dictionary, BufferedWriter output) throws IOException {
		if (!dictionary.containsKey(word.toLowerCase()))
		{
			output.write("Word not present.\n");
			output.flush();
			writeToDict(dictionary);

		}
		else
		{
			dictionary.remove(word.toLowerCase());
			output.write("Word deleted" + "\n");
			output.flush();
			writeToDict(dictionary);
		}
	}


}