package com.example.application.views.homepage;

//
//import com.vaadin.flow.component.Composite;
//import com.vaadin.flow.component.Text;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.H2;
//import com.vaadin.flow.component.html.Image;
//import com.vaadin.flow.component.html.Paragraph;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
//import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
//
//@PageTitle("Home")
//@Route(value = "")  // This is the default route
//public class HomePageView extends VerticalLayout {
//
//    public HomePageView() {
//    	 setSpacing(false);
//
//        // Add content to the home page
//        Div welcomeMessage = new Div(new Text("Welcome to the Buffalo Management System!"));
//        welcomeMessage.getStyle().set("font-size", "24px").set("font-weight", "bold");
//
//        //getContent().add(welcomeMessage);
//        
//       
//        Image img = new Image("images/buffalo.png", "placeholder plant");
//        img.setWidth("200px");
//        add(img);
//
//        H2 header = new H2("This place intentionally left empty");
//        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
//        add(header);
//        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
//
//        setSizeFull();
//        setJustifyContentMode(JustifyContentMode.CENTER);
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        getStyle().set("text-align", "center");
//    }
//}


//
//import com.vaadin.flow.component.Composite;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.html.Image;
//import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//
//@PageTitle("Home")
//@Route(value = "")
//public class HomePageView extends Composite<VerticalLayout> {
//
//    public HomePageView() {
//        // Access the main content layout
//        VerticalLayout layout = getContent();
//        layout.setSizeFull(); // Make the layout take the full viewport size
//        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER); // Center horizontally
//        //layout.setDefaultVerticalComponentAlignment(Alignment.CENTER); // Center vertically
//
//        // Add a 3D-like buffalo image
//        Image buffaloImage = new Image("images/empty-plant.png", "Buffalo 3D Image");
//        buffaloImage.setWidth("400px");
//        buffaloImage.setHeight("400px");
//        buffaloImage.setId("buffalo-image");
//
//        layout.add(buffaloImage);
//
//        // JavaScript animation using anime.js
//        String jsCode = """
//            // Ensure anime.js is loaded
//            if (typeof anime !== 'undefined') {
//                anime({
//                    targets: '#buffalo-image',
//                    translateY: [
//                        { value: -20, duration: 1000 },
//                        { value: 20, duration: 1000 }
//                    ],
//                    scale: [
//                        { value: 1.1, duration: 1000 },
//                        { value: 0.9, duration: 1000 }
//                    ],
//                    easing: 'easeInOutSine',
//                    loop: true
//                });
//            } else {
//                console.warn('anime.js is not loaded.');
//            }
//        """;
//
//        // Execute the JavaScript code after the page is loaded
//        UI.getCurrent().getPage().executeJs(jsCode);
//    }
//}

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle("Home")
@Route(" ")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/bodymovin/5.7.6/lottie.min.js")
@PermitAll
public class HomePageView extends Composite<VerticalLayout> {

    public HomePageView() {
        // Create the div where the Lottie animation will be rendered
    	 VerticalLayout layout = getContent();
         layout.setSizeFull(); // Make the layout fill the entire screen
         layout.setAlignItems(Alignment.CENTER); // Center horizontally
         layout.setJustifyContentMode(JustifyContentMode.CENTER); // Center vertically

         // Create a div where the Lottie animation will be rendered
         Div lottieContainer = new Div();
         lottieContainer.setId("lottie-animation");
         lottieContainer.getStyle().set("width", "100%");  // Full width
         lottieContainer.getStyle().set("height", "100vh"); // Full height (viewport height)
         lottieContainer.getStyle().set("display", "flex"); // Flexbox for centering
         lottieContainer.getStyle().set("align-items", "center");
         lottieContainer.getStyle().set("justify-content", "center");

         // Add the container to the layout
         layout.add(lottieContainer);
        // Call the Lottie animation loading script with the path to the JSON file in the downloads folder
        UI.getCurrent().getPage().executeJs(
            "lottie.loadAnimation({" +
                "container: document.getElementById('lottie-animation'), " +
                "renderer: 'svg', " +
                "loop: true, " +
                "autoplay: true, " +
                "path: './images/Animation - 1727352765057.json' " +  // Path to the JSON file
            "});"
        );
    }
}


