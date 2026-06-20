package sn.groupeisi.main;


import sn.groupeisi.entities.Category;
import sn.groupeisi.repositories.CategoryRepository;


public class Main {
    static void main(){
        CategoryRepository cr = new CategoryRepository();
        cr.create(Category.builder().name("Thriller").build());
        for (Category c : cr.getAllCategories()) {
            System.out.println(c);
        }
    }
}
