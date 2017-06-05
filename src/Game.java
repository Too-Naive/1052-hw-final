/*
 * This source code was published under GPL v3
 *
 * Copyright (C) 2017 Too-Naive
 *
 */


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Game extends JFrame{
	int aiLevel;
	static Random random = new Random();
	JPanel undertitleJPanel,gameJPanel,actionJPanel,menuJPanel;
	JButton aboutButton,newGameButton;
	JTextField statusField,gameStatusField;
	JButton scissorsButton,stoneButton,clothButton;
	StaticLanguage staticLanguage;
	int totalStatistics,winStatistics;
	public Game(){
		super("Scissors stone cloth");
		this.totalStatistics = 0;
		this.winStatistics = 0;
		this.setLanguage();
		this.setAilevel();
		this.setLayout(new BorderLayout(30,0));
		this.setSize(600,300);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.initUnderTitleJPanel();
		this.initMainJPanel();
		this.initActionJPanel();
		
		this.updateStatusField();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	void initUnderTitleJPanel(){
		this.undertitleJPanel = new JPanel();
		this.undertitleJPanel.setLayout(new GridLayout(1,2,10,20));
		this.menuJPanel = new JPanel();
		this.menuJPanel.setLayout(new GridLayout(1,2,10,20));

		this.statusField = new JTextField("normal");
		this.statusField.setHorizontalAlignment(JTextField.CENTER);
		this.statusField.setEditable(false);
		//this.statusField.requestFocus();
		this.statusField.setFocusable(false);
		this.undertitleJPanel.add(this.statusField);

		this.newGameButton = new JButton(this.staticLanguage.newGameString); //NEW GAME!
		this.newGameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGameTextField();
				totalStatistics = 0;
				winStatistics = 0;
			}
		});
		this.menuJPanel.add(this.newGameButton);

		this.aboutButton = new JButton(this.staticLanguage.aboutMeString);
		this.menuJPanel.add(this.aboutButton);

		this.undertitleJPanel.add(this.menuJPanel);
		this.add(this.undertitleJPanel,BorderLayout.NORTH);
	}

	void initMainJPanel(){
		this.gameJPanel = new JPanel();
		this.gameJPanel.setLayout(new GridLayout(1,1));

		this.gameStatusField = new JTextField("This is game text field");
		this.gameStatusField.setHorizontalAlignment(JTextField.CENTER);
		this.gameStatusField.setEditable(false);
		this.gameStatusField.setFocusable(false);
		this.gameJPanel.add(this.gameStatusField);

		this.add(this.gameJPanel,BorderLayout.CENTER);
	}

	void initActionJPanel(){
		this.actionJPanel = new JPanel();
		this.actionJPanel.setLayout(new GridLayout(1,3,10,20));

		this.scissorsButton = new JButton(this.staticLanguage.scissorsString);
		this.scissorsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mainGameProcess(0);
			}
		});
		this.actionJPanel.add(this.scissorsButton);

		this.stoneButton = new JButton(this.staticLanguage.stoneString);
		this.stoneButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mainGameProcess(1);	
			}
		});
		this.actionJPanel.add(this.stoneButton);

		this.clothButton = new JButton(this.staticLanguage.clothString);
		this.clothButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mainGameProcess(2);
			}
		});
		this.actionJPanel.add(this.clothButton);

		this.add(this.actionJPanel,BorderLayout.SOUTH);
	}

	private void mainGameProcess(int playerChoose){
		this.totalStatistics++;
		if (aiLevel==1){
			this.updateStatusField();
			switch(playerChoose){
				case 0:
					this.updateGameTextField(playerChoose, 1);
					break;
				case 1:
					this.updateGameTextField(playerChoose, 2);
					break;
				case 2:
					this.updateGameTextField(playerChoose, 0);
					break;
				default:
					RuntimeException runtimeException = new RuntimeException();
					throw runtimeException;
			}
			return ;
		}
		int aiChoose = this.callNext();
		int result = this.checkResult(playerChoose, aiChoose);
		if (result == 1)
			this.winStatistics++;
		this.updateStatusField();
		switch (result){
			case -1:
				this.updateGameTextField(playerChoose, aiChoose, this.staticLanguage.defeatString);
				break;
			case 0:
				this.updateGameTextField(playerChoose, aiChoose, this.staticLanguage.drawString);
				break;
			case 1:
				this.updateGameTextField(playerChoose, aiChoose, this.staticLanguage.victoryString);
				break;
			default:
				throw new RuntimeException();
		}
		throw new RuntimeException();
	}

	private void updateStatusField(){
		if (totalStatistics != 0)
			this.statusField.setText(String.format(this.staticLanguage.statusString, 
				this.totalStatistics,
				(float)this.winStatistics/(float)this.totalStatistics));
		else
			this.statusField.setText(String.format(this.staticLanguage.statusString,
				0,0.0));
	}
	private int checkResult(int playerChoose, int aiChoose){
		return playerChoose==aiChoose?0:(aiChoose>playerChoose||(aiChoose==0 && playerChoose==2))?-1:1;
	}
	private String choose2String(int choose){
		switch (choose){
			case 0:
				return this.staticLanguage.scissorsString;
			case 1:
				return this.staticLanguage.stoneString;
			case 2:
				return this.staticLanguage.stoneString;
			default:
				JOptionPane.showMessageDialog(null, "throw!","ERROR",JOptionPane.ERROR_MESSAGE);
				RuntimeException runtimeException = new RuntimeException();
				throw runtimeException;
				//assert(false);
		}
	}
	private void updateGameTextField(int playerChoose, int aiChoose){
		this.updateGameTextField(playerChoose,aiChoose,
			this.staticLanguage.defeatString);
	}
	private void updateGameTextField(int playerChoose, int aiChoose, String result){
		this.gameStatusField.setText(String.format(this.staticLanguage.resultString, 
			this.choose2String(playerChoose),this.choose2String(aiChoose),
			result));
	}
	private void updateGameTextField(){
		this.gameStatusField.setText("Click action button (Below these text) to start game");
	}
	private void setAilevel(){
		String[] options={"Low Level","High Level","Exit"};
		int result = JOptionPane.showOptionDialog(null,
						"Please select AI level You want",
						"Select AI Level",
						JOptionPane.DEFAULT_OPTION,
	  					JOptionPane.INFORMATION_MESSAGE,null,
						options,options[0]);
		switch (result){
			case 2:
			case -1:
				/**User select exit */
				System.exit(0);
			default:
				this.aiLevel = result;
		}
	}
	private void setLanguage(){
		String[] options={"Chinese(Traditional)","English(Simplified)"};
		int result = JOptionPane.showOptionDialog(null,
						"Please select Language",
						"Select Game Language",
						JOptionPane.DEFAULT_OPTION,
	  					JOptionPane.INFORMATION_MESSAGE,null,
						options,options[0]);
		switch (result){
			case -1:
				/**User select exit */
				System.exit(0);
			default:
				this.staticLanguage = new StaticLanguage(result==0?"zh":"");	
				this.setTitle(this.staticLanguage.titleString);
		}
	}

	private int callNext(){
		return random.nextInt(3);
	}
}


class StaticLanguage{
	public String statusString,resultString,scissorsString,stoneString,clothString;
	public String victoryString,defeatString,drawString;
	public String newGameString,aboutMeString;
	public String titleString;
	private void initString(String localeString){
		switch (localeString){
			case "zh":
				this.titleString = "剪刀石頭布";
				this.statusString = "共進行:%d場比賽 勝率:%.2f%%";
				this.resultString = "你:%s 電腦:%s 你%s了！";
				this.scissorsString = "剪刀";
				this.stoneString = "石頭";
				this.clothString = "布";
				this.victoryString = "獲勝";
				this.defeatString = "敗北";
				this.drawString = "平局";
				this.newGameString = "開始新遊戲";
				this.aboutMeString = "關於";
				break;
			default:
				this.titleString = "Scissors stone cloth";
				this.statusString = "Total:%d |"+
									"Winning percentage:%.2f%%";
				this.resultString = "You:%s AI:%s You %s !";
				this.scissorsString = "scissors";
				this.stoneString = "stone";
				this.clothString = "cloth";
				this.victoryString = "victory";
				this.defeatString = "defeat";
				this.drawString = "draw";
				this.newGameString = "New Game!";
				this.aboutMeString = "About";
		}
	}
	public StaticLanguage(){
		if (Locale.getDefault().toString().indexOf("zh") != -1) this.initString("zh");
		else this.initString("");
	}
	public StaticLanguage(String locale){
		this.initString(locale);
	}
}
