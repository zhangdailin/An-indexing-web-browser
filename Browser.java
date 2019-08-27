
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("serial")
public class Browser extends JFrame implements HyperlinkListener {
	private JTextField URLField;
	private JEditorPane contentPane;
	private WebDoc setUrl;
	private WebIndex b;
	private Set<String> saveUrl = new HashSet<>();
	private JButton backButton, forwardButton;
	private ArrayList<String> savepage = new ArrayList<>();

	public Browser() {
		getHistory();
		setTitle("Mini Browser");
		contentPane = new JEditorPane();
		JMenuBar mb = new JMenuBar();
		b = new WebIndex();
		String url = "http://google.co.uk";
		try {
			contentPane.setPage(url);
		} catch (IOException e) {
			showError("Attempted to read a bad URL: " + url);
		}

		contentPane.setEditable(false);
		contentPane.setContentType("text/html");
		contentPane.addHyperlinkListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(mb);
		// new menu
		JMenu function = new JMenu("Function");
		JMenuItem history = new JMenuItem("History");
		history.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyAction();
			}
		});

		JMenuItem Query = new JMenuItem("Query");
		Query.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog();
				dialog.setTitle("Type something to search: ");
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
				JTextField textfield = new JTextField(20);
				textfield.setBounds(10, 10, 40, 20);
				JButton goButton = new JButton("GO");
				panel.add(textfield);
				panel.add(goButton);
				goButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (textfield.getText() != null) {
							try {
								processData(contentPane, textfield.getText(), b).run();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				dialog.add(panel);
				dialog.setSize(300, 120);
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			}
		});
		mb.add(function);
		function.add(Query);
		function.add(history);
		JPanel buttonPanel = new JPanel();

		backButton = new JButton("Back ");
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(URLField.getText());
				int pagenum = savepage.indexOf(URLField.getText());
				try {
					contentPane.setPage(new URL(savepage.get(pagenum - 1)));
					URLField.setText(savepage.get(pagenum - 1));
					updateButtons();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		backButton.setEnabled(false);
		buttonPanel.add(backButton);

		forwardButton = new JButton("Forward ");
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(URLField.getText());
				int pagenum = savepage.indexOf(URLField.getText());
				try {
					contentPane.setPage(new URL(savepage.get(pagenum + 1)));
					URLField.setText(savepage.get(pagenum + 1));
					updateButtons();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		forwardButton.setEnabled(false);
		buttonPanel.add(forwardButton);

		URLField = new JTextField(35);
		buttonPanel.add(URLField);
		JButton goButton = new JButton("GO");
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (goButton()) {
					try {
						setUrl = new WebDoc(new URL(URLField.getText().toString()));
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
					setUrl.processUrl();
					b.add(setUrl);
				} else {
					showError("Attempted to read a bad URL: " + URLField.getText().toString());
				}
			}
		});
		buttonPanel.add(goButton);
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(contentPane));
		pack();

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		setSize(dim.width / 2, dim.height / 2);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					FileWriter fw = new FileWriter("history.txt");
					BufferedWriter out = new BufferedWriter(fw);
					for (int i = 0; i < savepage.size(); i++) {
						out.write(savepage.get(i) + "\n");
					}
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		});
	}

	private boolean goButton() {
		String Url = URLField.getText();
		try {
			contentPane.setPage(Url);
			savepage.add(Url);
			updateButtons();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void historyAction() {
		for (String key : b.map1.keySet()) {
			Iterator<WebDoc> a = b.map1.get(key).iterator();
			while (a.hasNext()) {
				saveUrl.add(a.next().url.toString());
			}
		}
		String createlink = new String();
		for (String link : saveUrl) {
			createlink = createlink + "<tr><a href=\"" + link + "\">" + link + "</a>" + "</tr>";
		}
		createlink = "<html><body>" + createlink + "</body></html>";
		contentPane.setText(createlink);
	}

	private void showError(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		HyperlinkEvent.EventType eventType = event.getEventType();
		if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
			if (event instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent linkEvent = (HTMLFrameHyperlinkEvent) event;
				HTMLDocument document = (HTMLDocument) contentPane.getDocument();
				document.processHTMLFrameHyperlinkEvent(linkEvent);
			} else {
				try {
					setUrl = new WebDoc(event.getURL());
					setUrl.processUrl();
					b.add(setUrl);
					contentPane.setPage(event.getURL());
					URLField.setText(event.getURL().toString());
					savepage.add(URLField.getText().toString());
					updateButtons();
				} catch (IOException e) {
					showError("Attempted to read a bad URL: " + event.getURL());
				}
			}
		}
	}

	private void updateButtons() {
		String currentUrl = URLField.getText();
		if (savepage.size() < 2) {
			backButton.setEnabled(false);
			forwardButton.setEnabled(false);
		} else {
			int pagenum = savepage.indexOf(currentUrl.toString());
			backButton.setEnabled(pagenum > 0);
			forwardButton.setEnabled(pagenum < (savepage.size() - 1));
		}
	}

	private static Thread processData(JEditorPane contentPane, String trim, WebIndex b) throws IOException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Set<String> a;
				QueryResult test = new QueryResult(b);
				a = test.processQuery(trim);

				if (a != null) {
					String createlink = new String();
					Iterator<String> c = a.iterator();

					while (c.hasNext()) {
						String link = c.next();
						createlink = createlink + "<tr><a href=\"" + link + "\">" + link + "</a>" + "</tr>";
					}
					createlink = "<html><body>" + createlink + "</body></html>";
					contentPane.setText(createlink);
				} else {
					JOptionPane.showMessageDialog(contentPane, "No result");
				}
			}
		});
		return t;
	}

	private void getHistory() {
		BufferedReader infile;
		String s;
		try {
			infile = new BufferedReader(new FileReader("history.txt"));
			b = new WebIndex();
			while ((s = infile.readLine()) != null) {
				setUrl = new WebDoc(new URL(s));
				setUrl.processUrl();
				b.add(setUrl);
				System.out.println(setUrl.toString());
			}
			for (String key : b.map1.keySet()) {
				Iterator<WebDoc> a = b.map1.get(key).iterator();
				while (a.hasNext()) {
					saveUrl.add(a.next().url.toString());
				}
			}
			infile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Browser browser = new Browser();
		browser.setVisible(true);
	}
}