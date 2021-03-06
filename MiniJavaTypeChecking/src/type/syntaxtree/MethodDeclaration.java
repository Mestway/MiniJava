/* Generated by JTB 1.4.7 */
package type.syntaxtree;

import type.visitor.*;

public class MethodDeclaration implements INode {

  public NodeToken f0;

  public Type f1;

  public Identifier f2;

  public NodeToken f3;

  public NodeOptional f4;

  public NodeToken f5;

  public NodeToken f6;

  public NodeListOptional f7;

  public NodeListOptional f8;

  public NodeToken f9;

  public Expression f10;

  public NodeToken f11;

  public NodeToken f12;

  private static final long serialVersionUID = 147L;

  public MethodDeclaration(final NodeToken n0, final Type n1, final Identifier n2, final NodeToken n3, final NodeOptional n4, final NodeToken n5, final NodeToken n6, final NodeListOptional n7, final NodeListOptional n8, final NodeToken n9, final Expression n10, final NodeToken n11, final NodeToken n12) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
    f7 = n7;
    f8 = n8;
    f9 = n9;
    f10 = n10;
    f11 = n11;
    f12 = n12;
  }

  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}
