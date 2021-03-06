package program;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import rubiks.*;
import search.AstarSearch;
import search.BFSearch;
import search.Searchable;

import java.awt.TextArea;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.DefaultComboBoxModel;

/**
 * Window to manually manipulate a rubik's cube, perturb it, and run A* or
 * Breadth-First Searches to find the path to the solved state.
 * 
 * @author Braemen Stoltz
 * @version
 */
public class CubeManipulationWindow extends JFrame
{
	private static final long 	serialVersionUID 	= -28901547254239848L;
	private RubiksCube 			cube				= null;
	private JFrame 				thisFrame 			= this;
	private JPanel 				contentPane 		= new JPanel();
	
	private JTextField 	face0Illustration 			= new JTextField();
	private JTextField 	face1Illustration 			= new JTextField();
	private JTextField 	face2Illustration 			= new JTextField();
	private JTextField 	face3Illustration 			= new JTextField();
	private JTextField 	face4Illustration 			= new JTextField();
	private JTextField 	face5Illustration 			= new JTextField();
	private JTextField 	perturbDepthTextField 		= new JTextField();
	private JTextField 	BFSearchTimeTextField 		= new JTextField();
	private JTextField 	AstarSearchTimeTextField 	= new JTextField();
	private TextArea 	face0TextArea 				= new TextArea();
	private TextArea 	face1TextArea 				= new TextArea();
	private TextArea 	face2TextArea 				= new TextArea();
	private TextArea 	face3TextArea 				= new TextArea();
	private TextArea 	face4TextArea 				= new TextArea();
	private TextArea 	face5TextArea 				= new TextArea();
	
	private String face0Str;
	private String face1Str;
	private String face2Str;
	private String face3Str;
	private String face4Str;
	private String face5Str;
	
	private JButton btnApplyMove 		= new JButton("Apply Move");;
	private JButton btnReset 			= new JButton("Reset Cube");
	private JButton btnBack 			= new JButton("Back");
	private JButton btnApplyAllMoves 	= new JButton("Apply all Moves");
	private JButton btnApplyOneMove 	= new JButton("Apply one Move");
	private JButton btnPerturb 			= new JButton("Perturb");
	
	private JComboBox<String> 	PerspectiveComboBox 		= new JComboBox<String>();
	private JTextPane 			recommendedMovesTextPane 	= new JTextPane();
	private JLabel 				lblInvalidDepth 			= new JLabel("invalid depth");
	private List<Move> 			recommendedMoves			= null;

	public void enable() { this.setVisible(true); }
	
	private String buildString(byte[][] arr, int face, String perspective) {
		String rtnStr = "";
		for(int i=0; i<arr.length; i++) {
			for(int j=0; j<arr[i].length; j++) {
				if(perspective.equals("From Top Right Corner"))
					rtnStr += (char)arr[i][j]+" ";
				else
					if(face == 0 || face == 4)
						rtnStr += (char)arr[i][this.cube.getSize()-1-j] + " ";
					else if(face == 2)
						rtnStr += (char)arr[this.cube.getSize()-1-i][j] + " ";
					else
						rtnStr += (char)arr[i][j]+" ";
			}
			rtnStr += "\n";
		}
		return rtnStr;
	}

	private void repaintCube(RubiksCube cube) {
		face0Str = buildString(cube.getCube()[0], 0, (String)PerspectiveComboBox.getSelectedItem());
		face1Str = buildString(cube.getCube()[1], 1, (String)PerspectiveComboBox.getSelectedItem());
		face2Str = buildString(cube.getCube()[2], 2, (String)PerspectiveComboBox.getSelectedItem());
		face3Str = buildString(cube.getCube()[3], 3, (String)PerspectiveComboBox.getSelectedItem());
		face4Str = buildString(cube.getCube()[4], 4, (String)PerspectiveComboBox.getSelectedItem());
		face5Str = buildString(cube.getCube()[5], 5, (String)PerspectiveComboBox.getSelectedItem());
		
		face0TextArea.setText(face0Str);
		face1TextArea.setText(face1Str);
		face2TextArea.setText(face2Str);
		face3TextArea.setText(face3Str);
		face4TextArea.setText(face4Str);
		face5TextArea.setText(face5Str);
	}
	
	public CubeManipulationWindow(RubiksCube rubiksCube) {
		setTitle					("Rubiks Cube Manipulation");
		setDefaultCloseOperation	(JFrame.EXIT_ON_CLOSE);
		setBounds					(100, 100, 862, 787);
		contentPane.setLayout		(null);
		contentPane.setBorder		(new EmptyBorder(5, 5, 5, 5));
		setContentPane				(contentPane);
		
		this.cube 		= rubiksCube;
		Move[] moveset 	= cube.getMoveSet();
		
		JButton 		btnBfsearch 			= new JButton("Breadth-First Search");
		JButton 		btnASearch 				= new JButton("A* Search");
		JButton 		btnSwitchPerspective 	= new JButton("Switch");
		JComboBox<Move> MoveSetDropDown 		= new JComboBox<Move>();
		JLabel 			lblIsFront 				= new JLabel("*5 is front face");
		JLabel 			lblIsBack 				= new JLabel("*4 is back face");		
		JLabel 			lblIsLeft 				= new JLabel("*0 is left face");		
		JLabel 			lblIsRight 				= new JLabel("*1 is right face");
		JLabel 			lblMoveSet 				= new JLabel("Move Set:");
		JLabel 			lblOrginization 		= new JLabel("Orginization:");
		JLabel 			lblFace 				= new JLabel("Face");
		JLabel 			lblIsBottom 			= new JLabel("*2 is bottom face");
		JLabel 			lblIsTop 				= new JLabel("*3 is top face");
		JLabel 			lblRecommendMoveSet 	= new JLabel("Recommended Moves From Search:");
		JLabel 			lblPerterbDepth 		= new JLabel("Perterb Depth:");
		JLabel 			lblPerspective 			= new JLabel("Perspective:");
		
		btnBfsearch.setFont				(new Font("Tahoma", Font.PLAIN, 25));
		btnBfsearch.setBounds			(378, 436, 266, 46);
		btnASearch.setFont				(new Font("Tahoma", Font.PLAIN, 25));
		btnASearch.setBounds			(480, 373, 164, 46);
		btnApplyMove.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		btnApplyMove.setBounds			(615, 94, 199, 63);
		btnReset.setFont				(new Font("Tahoma", Font.PLAIN, 25));
		btnReset.setBounds				(378, 20, 199, 63);
		btnBack.setFont					(new Font("Tahoma", Font.PLAIN, 25));
		btnBack.setBounds				(388, 94, 177, 63);
		btnSwitchPerspective.setFont	(new Font("Tahoma", Font.PLAIN, 20));
		btnSwitchPerspective.setBounds	(649, 170, 165, 31);
		btnPerturb.setFont				(new Font("Tahoma", Font.PLAIN, 25));
		btnPerturb.setBounds			(630, 296, 184, 46);
		btnApplyOneMove.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		btnApplyOneMove.setBounds		(590, 546, 224, 53);
		btnApplyAllMoves.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		btnApplyAllMoves.setBounds		(590, 602, 224, 53);

		lblPerspective.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		lblPerspective.setBounds		(504, 168, 141, 31);
		lblPerterbDepth.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		lblPerterbDepth.setBounds		(504, 254, 199, 31);
		lblRecommendMoveSet.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		lblRecommendMoveSet.setBounds	(378, 501, 436, 31);
		lblIsTop.setFont				(new Font("Tahoma", Font.PLAIN, 17));
		lblIsTop.setBounds				(31, 644, 128, 21);
		lblIsBottom.setFont				(new Font("Tahoma", Font.PLAIN, 17));
		lblIsBottom.setBounds			(31, 621, 141, 21);
		lblFace.setFont					(new Font("Tahoma", Font.PLAIN, 25));
		lblFace.setBounds				(80, 402, 58, 31);
		lblOrginization.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		lblOrginization.setBounds		(36, 436, 147, 31);
		lblMoveSet.setFont				(new Font("Tahoma", Font.PLAIN, 25));
		lblMoveSet.setBounds			(615, 20, 199, 31);
		lblIsRight.setFont				(new Font("Tahoma", Font.PLAIN, 17));
		lblIsRight.setBounds			(169, 698, 116, 21);
		lblIsLeft.setFont				(new Font("Tahoma", Font.PLAIN, 17));
		lblIsLeft.setBounds				(169, 676, 128, 21);
		lblIsBack.setFont				(new Font("Tahoma", Font.PLAIN, 17));
		lblIsBack.setBounds				(31, 686, 128, 21);
		lblIsFront.setFont				(new Font("Tahoma", Font.PLAIN, 17));
		lblIsFront.setBounds			(31, 665, 128, 21);
		lblInvalidDepth.setFont			(new Font("Tahoma", Font.PLAIN, 12));
		lblInvalidDepth.setForeground	(Color.RED);
		lblInvalidDepth.setBounds		(528, 347, 75, 15);
		lblInvalidDepth.setVisible		(false);
		
		PerspectiveComboBox.setFont			(new Font("Tahoma", Font.PLAIN, 20));
		PerspectiveComboBox.setModel		(new DefaultComboBoxModel<String>(new String[] {"From Top Right Corner", "Looking Directly at Face"}));
		PerspectiveComboBox.setSelectedIndex(0);
		PerspectiveComboBox.setBounds		(504, 204, 310, 39);
		
		//displaying the cube
		face0Str = buildString(cube.getCube()[0], 0, (String)PerspectiveComboBox.getSelectedItem());
		face1Str = buildString(cube.getCube()[1], 1, (String)PerspectiveComboBox.getSelectedItem());
		face2Str = buildString(cube.getCube()[2], 2, (String)PerspectiveComboBox.getSelectedItem());
		face3Str = buildString(cube.getCube()[3], 3, (String)PerspectiveComboBox.getSelectedItem());
		face4Str = buildString(cube.getCube()[4], 4, (String)PerspectiveComboBox.getSelectedItem());
		face5Str = buildString(cube.getCube()[5], 5, (String)PerspectiveComboBox.getSelectedItem());
		
		face0TextArea.setText		(face0Str);
		face0TextArea.setFont		(new Font("Consolas", Font.PLAIN, 20));
		face0TextArea.setEditable	(false);
		face0TextArea.setBounds		(36, 184, 150, 158);
		face1TextArea.setText		(face1Str);
		face1TextArea.setFont		(new Font("Consolas", Font.PLAIN, 20));
		face1TextArea.setEditable	(false);
		face1TextArea.setBounds		(348, 184, 150, 158);
		face2TextArea.setText		(face2Str);
		face2TextArea.setFont		(new Font("Consolas", Font.PLAIN, 20));
		face2TextArea.setEditable	(false);
		face2TextArea.setBounds		(192, 512, 150, 158);
		face3TextArea.setText		(face3Str);
		face3TextArea.setFont		(new Font("Consolas", Font.PLAIN, 20));
		face3TextArea.setEditable	(false);
		face3TextArea.setBounds		(192, 184, 150, 158);
		face4TextArea.setText		(face4Str);
		face4TextArea.setFont		(new Font("Consolas", Font.PLAIN, 20));
		face4TextArea.setEditable	(false);
		face4TextArea.setBounds		(192, 20, 150, 158);
		face5TextArea.setText		(face5Str);
		face5TextArea.setFont		(new Font("Consolas", Font.PLAIN, 20));
		face5TextArea.setEditable	(false);
		face5TextArea.setBounds		(192, 348, 150, 158);
		
		face0Illustration.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		face0Illustration.setEditable	(false);
		face0Illustration.setText		("0");
		face0Illustration.setBounds		(62, 501, 24, 31);
		face0Illustration.setColumns	(10);
		face1Illustration.setText		("3");
		face1Illustration.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		face1Illustration.setEditable	(false);
		face1Illustration.setColumns	(10);
		face1Illustration.setBounds		(88, 501, 24, 31);
		face2Illustration.setText		("4");
		face2Illustration.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		face2Illustration.setEditable	(false);
		face2Illustration.setColumns	(10);
		face2Illustration.setBounds		(88, 467, 24, 31);
		face3Illustration.setText		("1");
		face3Illustration.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		face3Illustration.setEditable	(false);
		face3Illustration.setColumns	(10);
		face3Illustration.setBounds		(114, 501, 24, 31);
		face4Illustration.setText		("2");
		face4Illustration.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		face4Illustration.setEditable	(false);
		face4Illustration.setColumns	(10);
		face4Illustration.setBounds		(88, 568, 24, 31);
		face5Illustration.setText		("5");
		face5Illustration.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		face5Illustration.setEditable	(false);
		face5Illustration.setColumns	(10);
		face5Illustration.setBounds		(88, 534, 24, 31);
		
		BFSearchTimeTextField.setEditable		(false);
		BFSearchTimeTextField.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		BFSearchTimeTextField.setBounds			(654, 436, 160, 46);
		BFSearchTimeTextField.setColumns		(10);
		AstarSearchTimeTextField.setFont		(new Font("Tahoma", Font.PLAIN, 25));
		AstarSearchTimeTextField.setEditable	(false);
		AstarSearchTimeTextField.setColumns		(10);
		AstarSearchTimeTextField.setBounds		(654, 374, 160, 46);
		recommendedMovesTextPane.setEditable	(false);
		recommendedMovesTextPane.setFont		(new Font("Tahoma", Font.PLAIN, 15));
		recommendedMovesTextPane.setBounds		(378, 545, 199, 192);
		perturbDepthTextField.setFont			(new Font("Tahoma", Font.PLAIN, 25));
		perturbDepthTextField.setBounds			(504, 296, 116, 46);
		perturbDepthTextField.setColumns		(10);
		
		for(Move move : moveset)
			MoveSetDropDown.addItem(move);
		MoveSetDropDown.setSelectedIndex(0);
		MoveSetDropDown.setBounds(615, 52, 199, 31);
		
		btnBfsearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BFSearch bfSearch = new BFSearch();
				try {
					double 				startTime 		= System.nanoTime();
					Searchable 			searchResult 	= bfSearch.search(cube, new RubiksCube(cube.getSize()));
					double 				endTime 		= System.nanoTime();
					double 				duration 		= (endTime - startTime)/1000000000;
					List<Searchable> 	path 			= bfSearch.getPath();
					if(searchResult == null)
						recommendedMovesTextPane.setText("Search did not\nfind a result");
					else {
						List<Move> moves = new ArrayList<Move>();
						for(Searchable obj : path)
							moves.add( ((RubiksCube)obj).getLastMoveApplied() );
						moves.remove(0);
						recommendedMoves = moves;
						for(Move move : recommendedMoves)
							recommendedMovesTextPane.setText(recommendedMovesTextPane.getText()+move+"\n");
						String 			pattern 		= "####.###";
						DecimalFormat 	decimalFormat 	= new DecimalFormat(pattern);
						BFSearchTimeTextField.setText(decimalFormat.format(duration)+"sec.");
					}
				} catch (Exception exc) {}
			}
		});
		
		btnASearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AstarSearch AstarSearch = new AstarSearch();
				try {
					double 				startTime 		= System.nanoTime();
					Searchable 			searchResult 	= AstarSearch.search(cube, new RubiksCube(cube.getSize()));
					double 				endTime 		= System.nanoTime();
					double 				duration 		= (endTime - startTime)/1000000000;
					List<Searchable> 	path 			= AstarSearch.getPath();
					if(searchResult == null)
						recommendedMovesTextPane.setText("Search did not\nfind a result");
					else {
						List<Move> moves = new ArrayList<Move>();
						for(Searchable obj : path)
							moves.add( ((RubiksCube)obj).getLastMoveApplied() );
						moves.remove(0);
						recommendedMoves = moves;
						for(Move move : recommendedMoves) {
							recommendedMovesTextPane.setText(recommendedMovesTextPane.getText()+move+"\n");
						}
						String 			pattern 		= "####.###";
						DecimalFormat 	decimalFormat 	= new DecimalFormat(pattern);
						AstarSearchTimeTextField.setText(decimalFormat.format(duration)+"sec.");
					}
				} catch(Exception exc) {}
			}
		});
		
		btnApplyMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Move move = (Move)MoveSetDropDown.getSelectedItem();
				move.apply(cube);
				repaintCube(cube);
				if(cube.isSolved()) {
					recommendedMoves.clear();
					recommendedMovesTextPane.setText("");
				}
				else {
					String textPaneContents = recommendedMovesTextPane.getText();
					recommendedMovesTextPane.setText("");
					String[] lines = textPaneContents.split("\n");
					if(lines[0].equals(move.toString())) {
						recommendedMoves.remove(0);
						for(int i=1; i<lines.length; i++) {
							recommendedMovesTextPane.setText(recommendedMovesTextPane.getText()+lines[i] + "\n");
						}
						if(recommendedMoves.isEmpty())
							recommendedMovesTextPane.setText("");
					}
				}
			}
		});
		
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RubiksCube newCube = new RubiksCube(cube.getSize());
				cube = newCube;
				repaintCube(cube);
			}
		});
		
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateCubeWindow window = new CreateCubeWindow();
				thisFrame.dispose();
				window.enable();
			}
		});
		
		btnApplyAllMoves.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(recommendedMoves!=null && !recommendedMoves.isEmpty()) {
					for(Move move : recommendedMoves) {
						move.apply(cube);
						repaintCube(cube);
					}
					recommendedMovesTextPane.setText("");
					recommendedMoves.clear();
				}
			}
		});
		
		btnApplyOneMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(recommendedMoves!=null && !recommendedMoves.isEmpty()) {
					Move move = recommendedMoves.remove(0);
					move.apply(cube);
					repaintCube(cube);
					String textPaneContents = recommendedMovesTextPane.getText();
					recommendedMovesTextPane.setText("");
					String[] lines = textPaneContents.split("\n");
					for(int i=1; i<lines.length; i++) {
						recommendedMovesTextPane.setText(recommendedMovesTextPane.getText()+lines[i] + "\n");
					}
					if(recommendedMoves.isEmpty())
						recommendedMovesTextPane.setText("");
				}
			}
		});
		
		btnPerturb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int 	depth 	= 0;
				boolean isValid = false;
				try {
					depth = Integer.parseInt(perturbDepthTextField.getText());
					isValid = true;
				} catch(Exception exc) {
					lblInvalidDepth.setVisible(true);
				}
				if(isValid) {
					lblInvalidDepth.setVisible(false);
					try {
						Perturber.perturb(depth, cube);
						repaintCube(cube);
					} catch(IllegalDepthException exc) {
						lblInvalidDepth.setVisible(true);
					}
				}
			}
		});
		
		btnSwitchPerspective.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				repaintCube(cube);
			}
		});
		
		contentPane.add(face0TextArea);
		contentPane.add(face1TextArea);
		contentPane.add(face2TextArea);
		contentPane.add(face3TextArea);
		contentPane.add(face4TextArea);
		contentPane.add(face5TextArea);
		contentPane.add(face0Illustration);
		contentPane.add(face1Illustration);
		contentPane.add(face2Illustration);
		contentPane.add(face3Illustration);
		contentPane.add(face4Illustration);
		contentPane.add(face5Illustration);
		contentPane.add(PerspectiveComboBox);
		contentPane.add(btnBfsearch);
		contentPane.add(btnASearch);
		contentPane.add(btnApplyMove);
		contentPane.add(btnReset);
		contentPane.add(btnBack);
		contentPane.add(MoveSetDropDown);
		contentPane.add(btnApplyAllMoves);
		contentPane.add(btnApplyOneMove);
		contentPane.add(recommendedMovesTextPane);
		contentPane.add(perturbDepthTextField);
		contentPane.add(btnPerturb);
		contentPane.add(BFSearchTimeTextField);
		contentPane.add(AstarSearchTimeTextField);
		contentPane.add(btnSwitchPerspective);
		contentPane.add(lblIsFront);
		contentPane.add(lblIsBack);
		contentPane.add(lblIsLeft);
		contentPane.add(lblIsRight);
		contentPane.add(lblInvalidDepth);
		contentPane.add(lblPerspective);
		contentPane.add(lblPerterbDepth);
		contentPane.add(lblRecommendMoveSet);
		contentPane.add(lblIsTop);
		contentPane.add(lblIsBottom);
		contentPane.add(lblFace);
		contentPane.add(lblOrginization);
		contentPane.add(lblMoveSet);
	}
}
