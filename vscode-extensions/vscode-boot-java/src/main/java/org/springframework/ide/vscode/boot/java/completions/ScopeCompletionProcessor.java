/*******************************************************************************
 * Copyright (c) 2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.java.completions;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.springframework.ide.vscode.commons.languageserver.completion.ICompletionProposal;
import org.springframework.ide.vscode.commons.util.text.IDocument;

/**
 * @author Martin Lippert
 */
public class ScopeCompletionProcessor {

	public void collectCompletionsForScopeAnnotation(ASTNode node, Annotation annotation, ITypeBinding type,
			List<ICompletionProposal> completions, int offset, IDocument doc) {
		
		try {
			if (node instanceof SimpleName && node.getParent() instanceof MemberValuePair) {
				MemberValuePair memberPair = (MemberValuePair) node.getParent();
				
				// case: @Scope(value=<*>)
				if (memberPair.getName() != null && memberPair.getValue().toString().equals("$missing$")) {
					for (ScopeNameCompletion completion : ScopeNameCompletionProposal.COMPLETIONS) {
						ICompletionProposal proposal = new ScopeNameCompletionProposal(completion, doc, offset, offset, "");
						completions.add(proposal);
					}
				}
			}
			// case: @Scope(<*>)
			else if (node == annotation && doc.get(offset - 1, 2).endsWith("()")) {
				for (ScopeNameCompletion completion : ScopeNameCompletionProposal.COMPLETIONS) {
					ICompletionProposal proposal = new ScopeNameCompletionProposal(completion, doc, offset, offset, "");
					completions.add(proposal);
				}
			}
			else if (node instanceof StringLiteral && node.getParent() instanceof Annotation) {
				// case: @Scope(value="")
				if (node.toString().startsWith("\"") && node.toString().endsWith("\"")) {
					String prefix = doc.get(node.getStartPosition(), offset - node.getStartPosition());
					for (ScopeNameCompletion completion : ScopeNameCompletionProposal.COMPLETIONS) {
						if (completion.getValue().startsWith(prefix)) {
							ICompletionProposal proposal = new ScopeNameCompletionProposal(completion, doc, node.getStartPosition(), node.getStartPosition() + node.getLength(), prefix);
							completions.add(proposal);
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
