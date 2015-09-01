/**
 * 
 */
package com.bu6ido.jsantase.boards;

import com.bu6ido.jsantase.common.PlayCard;

/**
 * @author bu6ido
 *
 */
public interface IBoard {

	public void print();
	public void fireCardMove(PlayCard card);
	public void fireTakeCards(boolean isOne);
}
