/*******************************************************************
 * Copyright (c) 2006 - 2007, Martin Kesting, All rights reserved.
 * 
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;


/**
 * Automatic closing of brackets and quotes.
 */
public class AutoCloseStrategy implements IAutoEditStrategy {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (d.getLength() == 0 ||
				c.offset == -1 || c.length > 0 || c.text == null || c.text.length() != 1) {
			return;
		}
		
		char ch = c.text.charAt(0);
		if (ch == '(') {
			setClosingChar(c, ')');
		}
		else if (ch == '{') {
			setClosingChar(c, '}');
		}
		else if (ch == '[') {
			setClosingChar(c, ']');
		}
		else if (ch == ')') {
			checkClosingChar(d, c, '(', ')');
		}
		else if (ch == '}') {
			checkClosingChar(d, c, '{', '}');
		}
		else if (ch == ']') {
			checkClosingChar(d, c, '[', ']');
		}
		else if (ch == '\"') {
			checkQuoteChar(d, c, '\"');
		}
		else if (ch == '\'') {
			checkQuoteChar(d, c, '\'');
		}
	}
	
	private void setClosingChar(DocumentCommand c, char closingChar) {
		c.text += closingChar;
		c.caretOffset = c.offset + 1;
		c.shiftsCaret = false;
	}
	
	private void checkQuoteChar(IDocument d, DocumentCommand c, char quoteChar) {
		try {
			if (c.offset == d.getLength()) { // cursor at eof
				if (countQuotes(d, c, quoteChar) %2 == 0){
					// set closing quote
					setClosingChar(c, quoteChar);
				}
				return;
			}
		
			char ch = d.getChar(c.offset);
			if (ch == quoteChar) {
				// next char is already quote
				c.caretOffset = c.offset + 1; // already there -> shift caret
				c.shiftsCaret = false;
				c.text = "";
			}
			else if (countQuotes(d, c, quoteChar) %2 == 0){
				// set closing quote
				setClosingChar(c, quoteChar);
			}
		} catch (BadLocationException e) {/* stop work */}
	}
	
	private void checkClosingChar(IDocument d, DocumentCommand c,
			char openingChar,  char closingChar) {
		if (c.offset == d.getLength()) {
			return; // cursor at eof -> nothing to do
		}
		
		try {
			char ch = d.getChar(c.offset);
			if (ch == closingChar) {
				// next char is already closing char
				// -> check, if it's already set by auto closing
				if (!needsClosingChar(d, c, openingChar, closingChar)) {
					c.caretOffset = c.offset + 1; // already there -> shift caret
					c.shiftsCaret = false;
					c.text = "";
				}
			}
		} catch (BadLocationException e) {/* stop work */}
	}
	
	private boolean needsClosingChar(IDocument d, DocumentCommand c,
			char openingChar,  char closingChar) throws BadLocationException {
		
		int countOpening = 0;
		int countClosing = 0;
		
		IRegion info = d.getLineInformationOfOffset(c.offset);
		
		int start = info.getOffset();
		int end   = c.offset;
		
		for (int i = start; i <= end; ++i) {
			char ch = d.getChar(i);
			if (ch == openingChar) {
				++countOpening;
			}
			else if (ch == closingChar) {
				++countClosing;
			}
		}
		
		return countOpening > countClosing;
	}
	
	private int countQuotes(IDocument d, DocumentCommand c, char quoteChar)
													throws BadLocationException {
		int count = 0;
		
		IRegion info = d.getLineInformationOfOffset(c.offset);
		
		int start = info.getOffset();
		int end   = c.offset;
		
		for (int i = start; i < end; ++i) {
			char ch = d.getChar(i);
			if (ch == quoteChar) {
				++count;
			}
		}
		
		return count;
	}
}
