package python;

import java.util.List;

public interface PythonASTNode {
    PythonASTNode getParent();

    void setParent(PythonASTNode parent);

    PythonASTNode getChild(int index);

    <T extends PythonASTNode> T getChild(int i, Class<? extends T> type, Class<? extends T> ...exclude);

    List<PythonASTNode> getChildren();
    
    <T extends PythonASTNode> List<T> getChildren(Class<? extends T> type, Class<? extends T> ...exclude);

    int getChildCount();
    
    <T extends PythonASTNode> int getChildCount(Class<? extends T> type, Class<? extends T> ...exclude);

    void addChild(PythonASTNode child);

    void resetChildren();

    String getText();

    void setText(String text);

    boolean isRoot();

    boolean isLeaf();
}
