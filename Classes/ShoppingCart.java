package Questions;
import java.util.*;
public class ShoppingCart {
    public static void main(String[] args) {
        Product p1=new Product("p1","Dell",50000,50);
        Product p2=new Product("p2","Kurti",500,10);
        Product p3=new Product("p3","Jeans",900,90);
        User u1=new User("u1", "MukundaPriya");
        Cart cart=u1.getCart();
        cart.addItem(p1,5);
        cart.addItem(p2, 10);
        cart.addItem(p3, 4);
        cart.viewCart();
        // cart.updateQty(p3, 0);



    }
}
class Product{
    private final String PID;
    private final String NAME;
    private final double PRICE;
    private int stockQty;
    public Product(String PID,String NAME,double PRICE,int stockQty){
        this.PID=PID;
        this.NAME=NAME;
        this.PRICE=PRICE;
        this.stockQty=stockQty;
    }
    public String getPid(){
        return PID;
    }
    public String getName(){
        return NAME;
    }
    public double getPrice(){
        return PRICE;
    }
    public int getQty(){
        return stockQty;
    }
    public void reduceQty(int qty){
        if(qty>stockQty){
            throw new IllegalArgumentException("Not Enough stock");
        }
        stockQty-=qty;
    } 
    public void increaseQty(int qty){
        stockQty+=qty;
    }
}
class CartItem{
    private Product product;
    private int qty;
    public CartItem(Product product, int qty){
        this.product=product;
        this.qty=qty;
    }
    public Product geProduct(){
        return product;
    }
    public int getQty(){
        return qty;
    }
    public void setQty(int newQty){
        if(newQty<=0)throw new IllegalArgumentException("Qty must be positive");
        this.qty=newQty;
    }
    public double getItemTotal(){
        return product.getPrice()*qty;
    }
}
abstract class Offer {
    private final String offId;
    private final String description;
    public Offer(String offId,String description){
        this.description=description;
        this.offId=offId;
    }
    public String getDesc(){
        return description;
    }
   public abstract double apply(Cart cart);
}
class FlatDiscount extends Offer{
    private final double discAmt;
    public FlatDiscount(String offId,String Description,double discAmt){
        super(offId, Description);
        this.discAmt=discAmt;
    }
    public double apply(Cart cart){
        return discAmt;
    }
}
// class PercentageDiscount extends Offer{
//     private final double DiscPercent;
//     public FlatDiscount(String offId,String Description,double DiscPercent){
//         super(offId, Description);
//         this.DiscPercent=DiscPercent;
//     }
//     public double apply(ShoppingCart cart){
//         return cart.getTotal()*DiscPercent/100;
//     }
// }
class Cart{
    private final Map<String,CartItem> items=new HashMap<>();
    private final List<Offer> offers = new ArrayList<>();
    public void addItem(Product product,int qty){
        if(product.getQty()<qty){
            System.out.println("Can't add to cart:Not enough stock for"+product.getName());
            return;
        }
        CartItem item=items.get(product.getPid());
        if(item==null){
            items.put(product.getPid(),new CartItem(product, qty));
        }
        else{
            item.setQty(item.getQty()+qty);
        }
        System.out.println("Added "+qty+" Of product"+product.getName()+"to cart");
    }
    public void removeItem(Product product){
        items.remove(product.getPid());
        System.out.println("removed "+product.getName()+" from the cart");
    }
    public void updateQty(Product product,int qty){
        CartItem item=items.get(product.getPid());
        if(item==null)return;
        if(qty<=0){
            items.remove(product.getPid());
        }
        else{
            item.setQty(qty);
        }
        System.out.println("Updated " + product.getName() + " quantity to " + qty + ".");

    }
    public void viewCart(){
        if(items==null)return;
        for(CartItem item:items.values()){
            System.out.println(item.geProduct().getName()+item.getQty());
        }
        System.out.println("cart subTotal= "+getsubTotal());
    }
    public void applyOffer(Offer offer){
        offers.add(offer);
        System.out.println("added offer "+offer.getDesc());
    }
    public double getsubTotal(){
        if(items==null)return 0;
        double total=0;
        for(CartItem item:items.values()){
            total+=item.getItemTotal();
        }
        return total;
    }
    public double getTotal(){
        double subTotal=getsubTotal();
        double totDis=0;
        for(Offer o:offers){
            totDis+=o.apply(this);
        }
        double tax = 0.1 * subTotal; // example: 10% tax
        double finalTotal = subTotal + tax - totDis;
        if (finalTotal < 0) finalTotal = 0;
        return finalTotal;
    }
    public Order checkOut(User user){
        for(CartItem item:items.values()){
            if(item.geProduct().getQty()<item.getQty()){
                System.out.println("cannot checkout "+item.geProduct().getName()+" Not enough quantity");
                return null;
            }

        }
        for(CartItem item:items.values()){
            item.geProduct().reduceQty(item.getQty());
        }
        Order order=new Order(UUID.randomUUID().toString(),user,new ArrayList<>(items.values()),getTotal());
        System.out.println("check out successful");
        items.clear();
        offers.clear();
        return order;
    }

}
class Order{
    private final String orderId;
    private final User user;
    private final List<CartItem> orderItems;
    private final double totalAmt;
    public Order(String orderId,User user,List<CartItem> orderItems,double totalAmt){
        this.orderId=orderId;
        this.user=user;
        this.orderItems=orderItems;
        this.totalAmt=totalAmt;
    }
    public String getOrderId(){
        return orderId;
    }
    public User getUser(){
        return user;
    }
    public double getTotalAmt(){
        return totalAmt;
    }

}
class User{
    private final String userId;
    private final String userName;
    private final Cart cart;
    public User(String userId,String userName){
        this.userId=userId;
        this.userName=userName;
        this.cart=new Cart();
    }
    public Cart getCart(){
        return cart;
    }
    public String getUserName(){
        return userName;
    }
    

}
