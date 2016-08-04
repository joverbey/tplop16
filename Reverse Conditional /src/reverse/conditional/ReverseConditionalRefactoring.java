package reverse.conditional;

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
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MultiTextEdit;

public class ReverseConditionalRefactoring extends Refactoring {

	private ICompilationUnit compilationUnit;
	private ASTNode selectedNode;
	private CompilationUnit compNode;
	private int offset, length;
	
	ReverseConditionalRefactoring() {}
	
	ReverseConditionalRefactoring(ICompilationUnit compilationUnit, ASTNode selectedNode, int offset, 
			int length) {
		this.compilationUnit = compilationUnit;
		this.selectedNode = selectedNode;
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
            parser.setSource(this.compilationUnit);
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
		if (this.selectedNode.getNodeType() != ASTNode.IF_STATEMENT) {
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
		CompilationUnitChange change = new CompilationUnitChange(this.getName(), this.compilationUnit);
		MultiTextEdit root = new MultiTextEdit();
		change.setEdit(root);
		
		new ReverseConditionalAlteration(this.compNode, this.offset, this.length).change(rewriter);
		root.addChild(rewriter.rewriteAST());
		return change;
	}
	
	public void setCompilationUnit(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
	
	public void setSelectedNode(ASTNode node) {
		this.selectedNode = node;
	}
	
	public void setOffsetLength(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}
}
