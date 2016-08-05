package reverse.conditional.c;

import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MultiTextEdit;

import reverse.conditional.ReverseConditionalRefactoring;
import reverse.conditional.java.ReverseConditionalJavaAlteration;

public class ReverseConditionalCRefactoring extends ReverseConditionalRefactoring {

	private IASTTranslationUnit AST;
	private IASTNode selectedNode;
	private CompilationUnit compNode;
	private int offset, length;
	
	ReverseConditionalCRefactoring() {}
	
	ReverseConditionalCRefactoring(IASTTranslationUnit AST, IASTNode selected, int offset, 
			int length) {
		this.AST = AST;
		this.selectedNode = selected;
		this.offset = offset;
		this.length = length;
	}
	
	@Override
	public String getName() {
		return "Reverse Conditional";
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		
		pm.beginTask("checkInitialConditions", 0);
		if (validNode()) {
			ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setSource(this.AST);
            parser.setResolveBindings(true);
            parser.setBindingsRecovery(true);
            this.compNode = (CompilationUnit) parser.createAST(pm);
		} else {
			status.addFatalError("Not a valid selection.");
		}
		pm.done();
		
		return status;
	}
	
	private boolean validNode() {
		if (this.selectedNode instanceof IASTIfStatement) {
			return false;
		} else if (((IfStatement) this.selectedNode).getElseStatement() == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		ASTRewrite rewriter = ASTRewrite.create(this.compNode.getAST());
		CompilationUnitChange change = new CompilationUnitChange(this.getName(), this.AST);
		MultiTextEdit root = new MultiTextEdit();
		change.setEdit(root);
		
		new ReverseConditionalJavaAlteration(this.compNode, this.offset, this.length).change(rewriter);
		root.addChild(rewriter.rewriteAST());
		return change;
	}
	
	public void setCompilationUnit(IASTTranslationUnit AST) {
		this.AST = AST;
	}
	
	public void setSelectedNode(IASTNode node) {
		this.selectedNode = node;
	}
	
	public void setOffsetLength(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}
}
