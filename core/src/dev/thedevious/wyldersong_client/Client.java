package dev.thedevious.wyldersong_client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
	public final String host;
	public final int port;
	private boolean shouldDisconnect = false;
	private final Game game;
	public boolean isReady = true;

	private PrintWriter writer;

	public Client(Game game, String host, int port) {
		this.host = host;
		this.port = port;
		this.game = game;
	}

	@Override
	public void run() {
		try (Socket socket = new Socket(host, port)) {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			Scanner scanner = new Scanner(System.in);

			this.writer = out;

			game.onConnect();

			while (!shouldDisconnect) {
				read(in);
				out.flush();
			}

			game.onDisconnect();

			scanner.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void read(BufferedReader reader) {
		try {
			String line = reader.readLine();

			if (line == null) return;

			System.out.println(line);

			game.onMessage(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(JSONObject object) {
		writer.println(object);
	}

	public void connect() {
		new Thread(this).start();
	}

	public void disconnect() {
		shouldDisconnect = true;
	}
}
