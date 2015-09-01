/**
 * 
 */
package com.bu6ido.jsantase.android_v3;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bu6ido.jsantase.Game;
import com.bu6ido.jsantase.boards.IBoard;
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
public class SantaseView extends View implements IBoard
{
	private List<Bitmap> origCardImages;
	private Bitmap origBackImage;
	private Bitmap origBackHorizImage;
	
	private List<Bitmap> cardImages;
	private Bitmap backImage;
	private Bitmap backHorizImage;
	
	private Point[] pointsPlayer1, pointsPlayer2;
	private Point pointOneCard, pointTwoCard;
	private Point pointDeck, pointTrump;
	
	private int mx = 0, my = 0;
	private PlayCard movingCard = null;
	
	private boolean isTaking = false;
	private int tx1 = 0, ty1 = 0, tx2 = 0, ty2 = 0;
	
	private Game game;
	
	public SantaseView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		loadCardImages();
	}

	public void setGame(Game game)
	{
		this.game = game;
	}
	
	protected void loadCardImages()
	{
		origCardImages = new ArrayList<Bitmap>();
		for (int i=0; i< (Cards.values().length * Suits.values().length); i++)
		{
			int resourceId = num2resourceId(i);
			Bitmap img = BitmapFactory.decodeResource(getResources(), resourceId);
			origCardImages.add(img);
		}
		origBackImage = BitmapFactory.decodeResource(getResources(), R.drawable.back);
		origBackHorizImage = BitmapFactory.decodeResource(getResources(), R.drawable.back_horiz);
	}
	
	protected void initPoints(int width)
	{   
		int cwidth = width / 8;
		pointsPlayer1 = new Point[6];
		for (int i=0; i<pointsPlayer1.length; i++)
		{
			float cx = (float) i * 1.4f * cwidth; 
			pointsPlayer1[i] = new Point((int)cx, 0);
		}
		pointsPlayer2 = new Point[6];
		for (int i=0; i<pointsPlayer2.length; i++)
		{
			float cx = (float) i * 1.4f * cwidth;
			pointsPlayer2[i] = new Point((int)cx, 8 + 2 * backImage.getHeight());
		}
		float cx = 2.8f * (float) cwidth;
		int cy = 4 + backImage.getHeight();
		pointOneCard = new Point((int) cx, cy);
		cx = 4.2f * (float) cwidth;
		pointTwoCard = new Point((int) cx, cy);
		pointDeck = new Point(0, cy);
		pointTrump = new Point(cwidth + 2, cy);
	}

	protected Bitmap scaleCard(Bitmap original, int width)
	{
		if (original == null)
		{
			return null;
		}
		float factor = (float) width / (float) original.getWidth();
		int height = Math.round(factor * (float) original.getHeight());
		Bitmap result = Bitmap.createScaledBitmap(original, width, height, true);
		return result;
	}

	protected void scaleCardImages(int width)
	{
		int cwidth = width / 8;
		cardImages = new ArrayList<Bitmap>();
		for (int i=0; i<origCardImages.size(); i++)
		{
			Bitmap img = origCardImages.get(i);
			cardImages.add(scaleCard(img, cwidth));
		}
		backImage = scaleCard(origBackImage, cwidth);
		backHorizImage = scaleCard(origBackHorizImage, (int)(1.4f * (float)cwidth) );
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		scaleCardImages(width);
		initPoints(width);
		
		canvas.drawColor(Color.MAGENTA);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1);

		// deck and trump
		if (game.deckHasCards())
		{
			canvas.drawBitmap(backImage, pointDeck.x, pointDeck.y, paint);
			if (game.getIsClosed())
			{
				canvas.drawBitmap(backHorizImage, pointDeck.x, pointDeck.y, paint);
			}
			else
			{
				canvas.drawBitmap((game.getTrumpCard() != null)? getCardImage(game.getTrumpCard()) : backImage, 
					pointTrump.x, pointTrump.y, paint);
			}
		}

		// playing cards
		canvas.drawRect(pointOneCard.x - 2, pointOneCard.y - 2, pointOneCard.x + backImage.getWidth() + 2, pointOneCard.y + backImage.getHeight() + 2, paint);
		if (game.getOneCard() != null)
		{
			if (isTaking)
			{
				canvas.drawBitmap(getCardImage(game.getOneCard()), tx1, ty1, paint);
			}
			else
			{
				canvas.drawBitmap(getCardImage(game.getOneCard()), pointOneCard.x, pointTwoCard.y, paint);
			}
		}
		canvas.drawRect(pointTwoCard.x - 2, pointTwoCard.y - 2, pointTwoCard.x + backImage.getWidth() + 2, pointTwoCard.y + backImage.getHeight() + 2, paint);
		if (game.getTwoCard() != null)
		{
			if (isTaking)
			{
				canvas.drawBitmap(getCardImage(game.getTwoCard()), tx2, ty2, paint);
			}
			else
			{
				canvas.drawBitmap(getCardImage(game.getTwoCard()), pointTwoCard.x, pointTwoCard.y, paint);
			}
		}
		
		// first player
		List<PlayCard> cardsPlayer1 = game.getPlayer1().getCards();
		for (int i=0; i<Math.min(cardsPlayer1.size(), pointsPlayer1.length); i++)
		{
			Bitmap imageCard = getCardImage(cardsPlayer1.get(i));
			if (cardsPlayer1.get(i).equals(movingCard))
			{
				canvas.drawBitmap(imageCard, mx, my, paint);
			}
			else
			{
				if (!game.isOne() || (game.getPlayer1() instanceof ComputerPlayer))
				{
					imageCard = backImage;
				}
				canvas.drawBitmap(imageCard, pointsPlayer1[i].x, pointsPlayer1[i].y, paint);
			}
		}
		// second player
		List<PlayCard> cardsPlayer2 = game.getPlayer2().getCards();
		for (int i=0; i<Math.min(cardsPlayer2.size(), pointsPlayer2.length); i++)
		{
			Bitmap imageCard = getCardImage(cardsPlayer2.get(i));
			if (cardsPlayer2.get(i).equals(movingCard))
			{
				canvas.drawBitmap(imageCard, mx, my, paint);
			}
			else
			{
				if (game.isOne() || (game.getPlayer2() instanceof ComputerPlayer))
				{
					imageCard = backImage;
				}
				canvas.drawBitmap(imageCard, pointsPlayer2[i].x, pointsPlayer2[i].y, paint);
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

	protected Bitmap getCardImage(PlayCard card)
	{
		int num = card2num(card);
		if ((num >= 0) && (num < cardImages.size()))
		{
			return cardImages.get(num);
		}
		return null;
	}

	protected int num2resourceId(int num)
	{
		switch (num)
		{
			case 0:
				return R.drawable.nine_clubs;
			case 1:
				return R.drawable.nine_diamonds;
			case 2:
				return R.drawable.nine_hearts;
			case 3:
				return R.drawable.nine_spades;
				
			case 4:
				return R.drawable.jack_clubs;
			case 5:
				return R.drawable.jack_diamonds;
			case 6:
				return R.drawable.jack_hearts;
			case 7:
				return R.drawable.jack_spades;
				
			case 8:
				return R.drawable.queen_clubs;
			case 9:
				return R.drawable.queen_diamonds;
			case 10:
				return R.drawable.queen_hearts;
			case 11:
				return R.drawable.queen_spades;
				
			case 12:
				return R.drawable.king_clubs;
			case 13:
				return R.drawable.king_diamonds;
			case 14:
				return R.drawable.king_hearts;
			case 15:
				return R.drawable.king_spades;
			
			case 16:
				return R.drawable.ten_clubs;
			case 17:
				return R.drawable.ten_diamonds;
			case 18:
				return R.drawable.ten_hearts;
			case 19:
				return R.drawable.ten_spades;
				
			case 20:
				return R.drawable.ace_clubs;
			case 21:
				return R.drawable.ace_diamonds;
			case 22:
				return R.drawable.ace_hearts;
			case 23:
				return R.drawable.ace_spades;
				
			default:
				return -1;
		}
	}

	@Override
	public void print() 
	{
		if (Looper.getMainLooper().getThread() == Thread.currentThread())
		{
			invalidate();
		}
		else
		{
			postInvalidate();
		}
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getAction() != MotionEvent.ACTION_UP)
		{
			return true;
		}
		final AbstractPlayer absPlayer1 = game.getPlayer1();
		final AbstractPlayer absPlayer2 = game.getPlayer2();
		
		if ((game.isOne() && !absPlayer1.isChoosing()) ||
			(!game.isOne() && !absPlayer2.isChoosing()) )
		{
			return true;
		}
		final int imgWidth = backImage.getWidth();
		final int imgHeight = backImage.getHeight();

		final int ex = (int) event.getX();
		final int ey = (int) event.getY();
		
		// change trump card
		if (!game.getIsClosed() && (game.getTrumpCard() != null))
		{
			if ((ex >= pointTrump.x) && (ex <= (pointTrump.x + imgWidth)) &&
				(ey >= pointTrump.y) && (ey <= (pointTrump.y + imgHeight)) )
			{
				AlertDialog dlg = new AlertDialog.Builder(getContext()).
					setTitle("Change trump card:").
					setMessage("Are you sure you want to change the trump card ?").
					setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							new Thread()
							{
								public void run() 
								{
									game.changeTrump(game.isOne()? absPlayer1 : absPlayer2);
								}
							}.start();
						}
					}).
					setNegativeButton("No", null).create();
				dlg.show();
				
				return true;
			}
		}
		
		// close deck
		if (!game.getIsClosed())
		{
			if ((ex >= pointDeck.x) && (ex <= pointDeck.x + imgWidth) &&
				(ey >= pointDeck.y) && (ey <= pointDeck.y + imgHeight))
			{
				AlertDialog dlg = new AlertDialog.Builder(getContext()).
					setTitle("Close game:").
					setMessage("Are you sure you want to close game ?").
					setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread()
							{
								@Override
								public void run() 
								{
									game.closeGame(game.isOne()? absPlayer1 : absPlayer2);
								}
							}.start();
						}
					}).
					setNegativeButton("No", null).create();
				dlg.show();
				
				return true;
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
			if ((ex >= p.x) && (ex <= (p.x + imgWidth)) &&
				(ey >= p.y) && (ey <= (p.y + imgHeight)) )
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
			
		return true;
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
		int imgHeight = backImage.getHeight();

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
