/*******************************************************************
 * Copyright (c) 2006 - 2007, Martin Kesting, All rights reserved.
 * 
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.rules;

import org.eclipse.jface.text.rules.IWordDetector;


/**
 * A Velocity directive aware word detector.
 */
public class DirectiveDetector implements IWordDetector {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char aChar) {
		return aChar == '#';
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == '-' || aChar == '_';
	}
}
