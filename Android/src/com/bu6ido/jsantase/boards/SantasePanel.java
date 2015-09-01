/**
 * 
 */
package com.bu6ido.jsantase.boards;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.common.PlayCard;
import com.bu6ido.jsantase.common.SantaseUtils;
import com.bu6ido.jsantase.enums.Cards;
import com.bu6ido.jsantase.enums.Suits;
import com.bu6ido.jsantase.players.AbstractPlayer;
import com.bu6ido.jsantase.players.ComputerPlayer;
import com.bu6ido.jsantase.players.GuiPlayer;

/**
 * @author bu6ido
 *
 */
public class SantasePanel extends JPanel implements IBoard
{
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	
	private List<Image> origCardImages;
	private Image origBackImage;
	private Image origBackHorizImage;
	
	private List<Image> cardImages;
	private Image backImage;
	private Image backHorizImage;
	
	private Point[] pointsPlayer1, pointsPlayer2;
	private Point pointOneCard, pointTwoCard;
	private Point pointDeck, pointTrump;
	
	private int mx = 0, my = 0;
	private PlayCard movingCard = null;
	
	private boolean isTaking = false;
	private int tx1 = 0, ty1 = 0, tx2 = 0, ty2 = 0;
	
	private Game game;
	public SantasePanel(JFrame frame, Game game)
	{
		this.frame = frame;
		this.game = game;
		loadCardImages();
		
		addListeners();
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		int width = getWidth();
		int height = getHeight();
		scaleCardImages(width);
		initPoints(width);
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, width, height);

		// deck and trump
		if (game.deckHasCards())
		{
			g.drawImage(backImage, pointDeck.x, pointDeck.y, null);
			if (game.getIsClosed())
			{
				g.drawImage(backHorizImage, pointDeck.x, pointDeck.y, null);
			}
			else
			{
				g.drawImage((game.getTrumpCard() != null)? getCardImage(game.getTrumpCard()) : backImage, 
					pointTrump.x, pointTrump.y, null);
			}
		}

		// playing cards
		g.setColor(Color.BLACK);
		g.drawRect(pointOneCard.x - 2, pointOneCard.y - 2, backImage.getWidth(null) + 4, backImage.getHeight(null) + 4);
		if (game.getOneCard() != null)
		{
			if (isTaking)
			{
				g.drawImage(getCardImage(game.getOneCard()), tx1, ty1, null);
			}
			else
			{
				g.drawImage(getCardImage(game.getOneCard()), pointOneCard.x, pointOneCard.y, null);
			}
		}
		g.drawRect(pointTwoCard.x - 2, pointTwoCard.y - 2, backImage.getWidth(null) + 4, backImage.getHeight(null) + 4);
		if (game.getTwoCard() != null)
		{
			if (isTaking)
			{
				g.drawImage(getCardImage(game.getTwoCard()), tx2, ty2, null);
			}
			else
			{
				g.drawImage(getCardImage(game.getTwoCard()), pointTwoCard.x, pointTwoCard.y, null);
			}
		}
		
		// first player
		List<PlayCard> cardsPlayer1 = game.getPlayer1().getCards();
		for (int i=0; i<Math.min(cardsPlayer1.size(), pointsPlayer1.length); i++)
		{
			Image imageCard = getCardImage(cardsPlayer1.get(i));
			if (cardsPlayer1.get(i).equals(movingCard))
			{
				g.drawImage(imageCard, mx, my, null);
			}
			else
			{
				if (!game.isOne() || (game.getPlayer1() instanceof ComputerPlayer))
				{
					imageCard = backImage;
				}
				g.drawImage(imageCard, pointsPlayer1[i].x, pointsPlayer1[i].y, null);
			}
		}
		// second player
		List<PlayCard> cardsPlayer2 = game.getPlayer2().getCards();
		for (int i=0; i<Math.min(cardsPlayer2.size(), pointsPlayer2.length); i++)
		{
			Image imageCard = getCardImage(cardsPlayer2.get(i));
			if (cardsPlayer2.get(i).equals(movingCard))
			{
				g.drawImage(imageCard, mx, my, null);
			}
			else
			{
				if (game.isOne() || (game.getPlayer2() instanceof ComputerPlayer))
				{
					imageCard = backImage;
				}
				g.drawImage(imageCard, pointsPlayer2[i].x, pointsPlayer2[i].y, null);
			}
		}
	}
	
	protected void initPoints(int width)
	{   
		int cwidth = width / 8;
		pointsPlayer1 = new Point[6];
		for (int i=0; i<pointsPlayer1.length; i++)
		{
			float cx = (float) i * 1.4f * cwidth; 
			pointsPlayer1[i] = new Point((int)cx, 5);
		}
		pointsPlayer2 = new Point[6];
		for (int i=0; i<pointsPlayer2.length; i++)
		{
			float cx = (float) i * 1.4f * cwidth;
			pointsPlayer2[i] = new Point((int)cx, 20 + 2 * backImage.getHeight(null));
		}
		float cx = 2.8f * (float) cwidth;
		int cy = 10 + backImage.getHeight(null);
		pointOneCard = new Point((int) cx, cy);
		cx = 4.2f * (float) cwidth;
		pointTwoCard = new Point((int) cx, cy);
		pointDeck = new Point(0, cy);
		pointTrump = new Point(cwidth + 2, cy);
	}
	
	protected void loadCardImages()
	{
		origCardImages = new ArrayList<Image>();
		for (int i=0; i<24; i++)
		{
			String imageName = num2imageName(i);
			Image img = SantaseUtils.loadImage(imageName);
			origCardImages.add(img);
		}
		origBackImage = SantaseUtils.loadImage("back.png");
		origBackHorizImage = SantaseUtils.loadImage("back_horiz.png");
	}
	
	protected Image scaleCard(Image img, int width)
	{
		float factor = (float) width / (float) img.getWidth(null);
		Image result = img.getScaledInstance(width, Math.round(factor * (float)img.getHeight(null)), Image.SCALE_DEFAULT);
		return result;
	}
	
	protected void scaleCardImages(int width)
	{
		int cwidth = width / 8;
		cardImages = new ArrayList<Image>();
		for (int i=0; i<origCardImages.size(); i++)
		{
			Image img = origCardImages.get(i);
			cardImages.add(scaleCard(img, cwidth));
		}
		backImage = scaleCard(origBackImage, cwidth);
		backHorizImage = scaleCard(origBackHorizImage, (int)(1.4f * (float)cwidth) );
	}
	
	protected void addListeners()
	{
		addMouseMotionListener(new MouseMotionAdapter() 
		{
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				
				setCursor(defCursor);
				setToolTipText(null);
				
				final AbstractPlayer absPlayer1 = game.getPlayer1();
				final AbstractPlayer absPlayer2 = game.getPlayer2();
				
				if ((game.isOne() && !absPlayer1.isChoosing()) ||
					(!game.isOne() && !absPlayer2.isChoosing()) )
				{
					return;
				}
				int imgWidth = backImage.getWidth(null);
				int imgHeight = backImage.getHeight(null);
				
				int mx = e.getX();
				int my = e.getY();
				Point p = null;
				for (int i=0; i<pointsPlayer1.length; i++)
				{
					p = pointsPlayer1[i];
					if ((mx >= p.x) && (mx <= (p.x + imgWidth)) &&
						(my >= p.y) && (my <= (p.y + imgHeight)) )
					{
						setCursor(handCursor);
						if (game.isOne())
						{
							setToolTipText("Choose card");
						}
					}
				}
				
				for (int i=0; i<pointsPlayer2.length; i++)
				{
					p = pointsPlayer2[i];
					if ((mx >= p.x) && (mx <= (p.x + imgWidth)) &&
						(my >= p.y) && (my <= (p.y + imgHeight)) )
					{
						setCursor(handCursor);
						if (!game.isOne())
						{
							setToolTipText("Choose card");
						}
					}
				}
				
				p = pointTrump;
				if ((mx >= p.x) && (mx <= (p.x + imgWidth)) &&
					(my >= p.y) && (my <= (p.y + imgHeight)) )
				{
					setCursor(handCursor);
					setToolTipText("Change trump");
				}
				
				p = pointDeck;
				if ((mx >= p.x) && (mx <= (p.x + imgWidth)) &&
					(my >= p.y) && (my <= (p.y + imgHeight)) )
				{
					setCursor(handCursor);
					setToolTipText("Close game");
				}
			}
			
		});
		
		addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				final AbstractPlayer absPlayer1 = game.getPlayer1();
				final AbstractPlayer absPlayer2 = game.getPlayer2();
				
				if ((game.isOne() && !absPlayer1.isChoosing()) ||
					(!game.isOne() && !absPlayer2.isChoosing()) )
				{
					return;
				}
				int imgWidth = backImage.getWidth(null);
				int imgHeight = backImage.getHeight(null);
				
				// change trump card
				if (!game.getIsClosed() && (game.getTrumpCard() != null))
				{
					if ((e.getX() >= pointTrump.x) && (e.getX() <= (pointTrump.x + imgWidth)) &&
						(e.getY() >= pointTrump.y) && (e.getY() <= (pointTrump.y + imgHeight)) )
					{
						int res = SantaseUtils.dialogYesNo(frame, "Change trump card:", "Are you sure you want to change the trump card ?", 
							new String[] { "Yes", "No" });
						if (res == 0)
						{
							AbstractPlayer changePlayer = game.isOne()? absPlayer1 : absPlayer2; 
							game.changeTrump(changePlayer);
							game.getNotification().info(changePlayer + " changed the trump card.");
							return;
						}
					}
				}
				
				// close deck
				if (!game.getIsClosed())
				{
					if ((e.getX() >= pointDeck.x) && (e.getX() <= pointDeck.x + imgWidth) &&
						(e.getY() >= pointDeck.y) && (e.getY() <= pointDeck.y + imgHeight))
					{
						int res = SantaseUtils.dialogYesNo(frame, "Close game:", "Are you sure you want to close game ?", 
							new String[] { "Yes", "No" });
						if (res == 0)
						{
							game.closeGame(game.isOne()? absPlayer1 : absPlayer2);
						}
						return;
					}
				}
				
				List<PlayCard> cards;
				Point[] points;
				
				if (game.isOne())
				{
					cards = absPlayer1.getCards();
					points = pointsPlayer1;
				}
				else
				{
					cards = absPlayer2.getCards();
					points = pointsPlayer2;
				}
				
				int index = -1;
				for (int i=0; i<points.length; i++)
				{
					Point p = points[i];
					if ((e.getX() >= p.x) && (e.getX() <= (p.x + imgWidth)) &&
						(e.getY() >= p.y) && (e.getY() <= (p.y + imgHeight)) )
					{
						index = i;
						break;
					}
				}
				if ((index >= 0) && (index < cards.size()))
				{
					PlayCard card = cards.get(index);
					
					if (game.isOne())
					{
						if (absPlayer1 instanceof GuiPlayer)
						{
							GuiPlayer guiPlayer1 = (GuiPlayer) absPlayer1;
							guiPlayer1.setCard(card);
						}
					}
					else
					{
						if (absPlayer2 instanceof GuiPlayer)
						{
							GuiPlayer guiPlayer2 = (GuiPlayer) absPlayer2;
							guiPlayer2.setCard(card);
						}
					}					
				}
			}
		});
	}
	
	public void print()
	{
		SantaseUtils.waitUntilFinish(new Runnable() {
			
			@Override
			public void run() 
			{
				repaint();
			}
		});
	}
	
	public void move(final int x1, final int y1, final int x2, final int y2)
	{
		Point[] points = SantaseUtils.linePath(x1, y1, x2, y2, 10);
		if (points != null)
		{
			for (int i=0; i<points.length; i++)
			{
				Point p = points[i];
				mx = p.x;
				my = p.y;
				print();
				SantaseUtils.sleep(50);
			}
		}
	}
	
	protected int card2num(PlayCard card)
	{
		Integer numSuits = Suits.values().length;
		
		Suits s = card.getSuit();
		Cards c = card.getCard();
		
		return c.ordinal() * numSuits + s.ordinal();
	}
	
	protected Image getCardImage(PlayCard card)
	{
		int num = card2num(card);
		if ((num >= 0) && (num < cardImages.size()))
		{
			return cardImages.get(num);
		}
		return null;
	}
	
	protected String num2imageName(Integer num)
	{
		String[] cardNames = { "nine", "jack", "queen", "king", "ten", "ace" };
		String[] suitNames = { "clubs", "diamonds", "hearts", "spades" };
		Integer numSuits = Suits.values().length;
		
		int c = num / numSuits;
		int s = num % numSuits;
		
		return cardNames[c] + "_" + suitNames[s] + ".png";
	}
	
	@Override
	public void fireCardMove(PlayCard card)
	{
		Point[] points = game.isOne()? pointsPlayer1 : pointsPlayer2;
		int index = game.isOne()? game.getPlayer1().getCards().indexOf(card) :
				game.getPlayer2().getCards().indexOf(card);
		Point dest = game.isOne()? pointOneCard : pointTwoCard;
		if (index >= 0)
		{
			movingCard = card;
			move(points[index].x, points[index].y, dest.x, dest.y);
		}
	}
	
	@Override
	public void fireTakeCards(boolean isOne) 
	{
		int imgHeight = backImage.getHeight(null);

		Point[] pone = SantaseUtils.linePath(pointOneCard.x, pointOneCard.y, pointOneCard.x, isOne? -imgHeight : getHeight(), 4);
		Point[] ptwo = SantaseUtils.linePath(pointTwoCard.x, pointTwoCard.y, pointTwoCard.x, isOne? -imgHeight : getHeight(), 4);
		
		isTaking = true;
		for (int i=0; i<Math.min(pone.length, ptwo.length); i++)
		{
			tx1 = pone[i].x;
			ty1 = pone[i].y;
			tx2 = ptwo[i].x;
			ty2 = ptwo[i].y;
			print();
			SantaseUtils.sleep(50);
		}
		isTaking = false;
	}
}
