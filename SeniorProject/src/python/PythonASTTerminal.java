package python;

import java.util.Collections;
import java.util.List;

public class PythonASTTerminal extends AbstractPythonASTNode {
    
    public static final class PythonASTIndent extends PythonASTTerminal {
        public PythonASTIndent() {
            super("");
        }

        @Override
        public void setText(String text) {
            throw new UnsupportedOperationException();
        }
    }
    
    public static final class PythonASTDedent extends PythonASTTerminal {
        public PythonASTDedent() {
            super("");
        }

        @Override
        public void setText(String text) {
            throw new UnsupportedOperationException();
        }
    }
    
    public PythonASTTerminal(String text) {
        super(text);
    }

    @Override
    public PythonASTNode getChild(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends PythonASTNode> T getChild(int i, Class<? extends T> type, Class<? extends T>... exclude) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PythonASTNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public <T extends PythonASTNode> List<T> getChildren(Class<? extends T> type, Class<? extends T>... exclude) {
        return Collections.emptyList();
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public <T extends PythonASTNode> int getChildCount(Class<? extends T> type, Class<? extends T>... exclude) {
        return 0;
    }

    @Override
    public void addChild(PythonASTNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
