package intermediate;

import java.util.List;

/**
 * Intermediate AST node that represents an entire compilation unit.
 * Instances of this class will always be root nodes, though the
 * root of the AST may not always be an instance of this class.
 */
public class IntASTCompilationUnit extends AbstractIntASTBranchNode {
    public IntASTCompilationUnit() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child == null) {
            return;
        } else if (child instanceof IntASTPackage) {
            // adding a package declaration, so ensure one doesn't
            // already exist
            if (super.children.isEmpty() || !(super.children.get(0) instanceof IntASTPackage)) {
                // no existing declaration, so add the new child
                child.setParent(this);
                super.children.add(0, child);
            } else {
                // this compilation unit already has a package
                // declaration, so throw an exception
                throw new IllegalArgumentException("Cannot have more than one package declaration per compilation unit");
            }
        } else if (child instanceof IntASTImport) {
            // adding an import declaration, so ensure it gets inserted
            // into the correct spot in the children list
            child.setParent(this);
            if (super.children.isEmpty() ||
                    (super.children.get(super.children.size()-1) instanceof IntASTPackage) ||
                    (super.children.get(super.children.size()-1) instanceof IntASTImport)) {
                // insert normally
                super.children.add(child);
            } else {
                // find the last instance of a package or import
                // declaration and insert the new import after that
                for (int i = super.children.size(); i > 0; i--) {
                    if ((super.children.get(i-1) instanceof IntASTPackage) ||
                            (super.children.get(i-1) instanceof IntASTImport)) {
                        super.children.add(i, child);
                        break;
                    }
                }
            }
        } else if (child instanceof IntASTClass) {
            // adding a class declaration. this goes after everything
            // else and order doesn't really matter.
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("intermediate.IntASTCompilationUnit does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    // convenience methods

    public IntASTPackage getPackageDeclaration() {
        return getChild(0, IntASTPackage.class);
    }

    public IntASTImport getImportDeclaration(int i) {
        return getChild(i, IntASTImport.class);
    }

    public List<IntASTImport> getImportDeclaration() {
        return getChildren(IntASTImport.class);
    }

    public IntASTClass getClassDeclaration(int i) {
        return getChild(i, IntASTClass.class);
    }

    public List<IntASTClass> getClassDeclaration() {
        return getChildren(IntASTClass.class);
    }
}
