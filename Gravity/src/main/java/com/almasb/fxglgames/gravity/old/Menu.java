package com.almasb.fxglgames.gravity.old;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

public class Menu extends Parent {

    private ObservableList<Node> menuRoot;

    public final SimpleBooleanProperty newGameEvent = new SimpleBooleanProperty();

    public Menu() {
        Region bg = new Region();
        bg.prefWidthProperty().bind(Config.appWidth);
        bg.prefHeightProperty().bind(Config.appHeight);

        bg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text textLogo = new Text("GRAVITY");
        textLogo.setFont(Config.Fonts.LOGO);
        textLogo.setFill(Color.WHITE);
        textLogo.setTranslateX(Config.APP_W / 2 - 200);
        textLogo.setTranslateY(150);

        Group menuGroup = new Group();
        menuRoot = menuGroup.getChildren();

        // main menu
        MenuItem itemResume = new MenuItem("New Game");
        itemResume.setOnMouseClicked(event -> {
            newGameEvent.set(true);
            itemResume.setEnabled(false);
        });
        MenuItem itemContinue = new MenuItem("Continue");
        itemContinue.setEnabled(false);
        MenuItem itemOptions = new MenuItem("Options");
        MenuItem itemCredits = new MenuItem("Credits");
        itemCredits.setMenuContent(new MenuContent() {
            @Override
            protected void createContent(VBox vbox) {
                Text textHead = new Text("This project makes use of:");
                textHead.setFont(Config.FONT);
                textHead.setFill(Color.WHITE);

                Text textJFX = new Text("Graphics Library and Application Framework: JavaFX 8.0.25");
                textJFX.setFont(Config.FONT);
                textJFX.setFill(Color.WHITE);

                Text textJBOX = new Text("Physics Engine: JBox2d 2.2.1.1 (jbox2d.org)");
                textJBOX.setFont(Config.FONT);
                textJBOX.setFill(Color.WHITE);

                Text textRES = new Text("Various in-game resources: (opengameart.org)");
                textRES.setFont(Config.FONT);
                textRES.setFill(Color.WHITE);

                vbox.getChildren().addAll(textHead, textJFX, textJBOX, textRES);
            }
        });
        MenuItem itemExit = new MenuItem("Exit");
        itemExit.setOnMouseClicked(event -> System.exit(0));

        MenuBox menuMain = new MenuBox(itemResume, itemContinue, itemOptions, itemCredits, itemExit);

        // options
        MenuItem itemControls = new MenuItem("Controls");
        itemControls.setMenuContent(new MenuContent() {
            @Override
            protected void createContent(VBox vbox) {
                Text textHead = new Text("Here players will be able to change key bindings");
                textHead.setFont(Config.FONT);
                textHead.setFill(Color.WHITE);

                vbox.getChildren().add(textHead);
            }
        });
        MenuItem itemVideo = new MenuItem("Video");
        itemVideo.setMenuContent(new MenuContent() {
            @Override
            protected void createContent(VBox vbox) {
                Text textHead = new Text("Resolutions:");
                textHead.setFont(Config.FONT);
                textHead.setFill(Color.WHITE);

                vbox.getChildren().add(textHead);

                Screen screen = Screen.getPrimary();
                int maxWidth = (int)screen.getBounds().getWidth();
                int maxHeight = (int)screen.getBounds().getHeight();

                for (int i = 0; i <= 5; i += 2) {
                    float ratio = 1 + i*0.1f;
                    VideoMode mode = new VideoMode((int)(Config.APP_W * ratio), (int)(Config.APP_H * ratio), ratio);

                    if (mode.width <= maxWidth && mode.height <= maxHeight) {
                        MenuItem textMode = new MenuItem(mode.width + " X " + mode.height);
                        textMode.setOnMouseClicked(event -> {
                            Config.resolutionScale.set(mode.ratio);
                        });
                        vbox.getChildren().add(textMode);
                    }
                }
            }
        });
        MenuItem itemAudio = new MenuItem("Audio");
        itemAudio.setMenuContent(new MenuContent() {
            @Override
            protected void createContent(VBox vbox) {
                Slider slider = new Slider(0, 1.0, 0.5);
                Config.volume.bind(slider.valueProperty());

                Text textVolume = new Text();
                textVolume.setFont(Config.FONT);
                textVolume.setFill(Color.WHITE);
                textVolume.textProperty().bind(new SimpleStringProperty("Volume: ").concat(slider.valueProperty().multiply(100).asString("%.0f")).concat("%"));

                HBox boxVolume = new HBox(slider, textVolume);
                boxVolume.setAlignment(Pos.CENTER);

                vbox.getChildren().addAll(boxVolume);
            }
        });

        MenuBox menuOptions = new MenuBox(itemControls, itemVideo, itemAudio);

        // add relationships
        itemOptions.setChildMenu(menuOptions);

        menuRoot.add(menuMain);
        menuRoot.add(new MenuContent() {
            @Override
            protected void createContent(VBox vbox) {
            }
        });

        switchMenuTo(menuMain);
        getChildren().addAll(bg, textLogo, menuGroup);
    }

    private void switchMenuTo(MenuBox menu) {
        menu.setTranslateX(200);
        menu.setTranslateY(300);
        menuRoot.set(0, menu);
    }

    private void switchMenuContentTo(MenuContent content) {
        content.setTranslateX(700);
        content.setTranslateY(300);
        menuRoot.set(1, content);
    }

    private abstract class MenuContent extends Parent {
        protected abstract void createContent(VBox vbox);

        public MenuContent() {
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(50, 0, 0, 0));
            createContent(vbox);
            getChildren().add(vbox);
        }
    }

    private class MenuBox extends VBox {
        public MenuBox(MenuItem... items) {
            super(items);
            for (MenuItem item : items) {
                item.setMenuParent(this);
            }
        }
    }

    /**
     * Menu item will do one of the following:
     *
     * 1. some action
     * 2. open child menu
     * 3. show menu content;
     */
    private class MenuItem extends Parent {
        private MenuBox menuParent;
        private MenuContent menuContent;

        public MenuItem(String name) {
            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font(48));

            getChildren().addAll(text);

            this.setOnMouseEntered(event -> {
                text.setStroke(Color.BLUE);
            });

            this.setOnMouseExited(event -> {
                text.setStroke(null);
            });

            this.setOnMousePressed(event -> {
                this.setEffect(new Glow(1));
            });

            this.setOnMouseReleased(event -> {
                this.setEffect(null);
            });
        }

        public void setText(String name) {
            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font(48));
            getChildren().set(0, text);
        }

        public void setMenuParent(MenuBox menu) {
            menuParent = menu;
        }

        public void setMenuContent(MenuContent content) {
            menuContent = content;
            this.setOnMouseClicked(event -> {
                switchMenuContentTo(menuContent);
            });
        }

        public MenuBox getMenuParent() {
            return menuParent;
        }

        public MenuContent getMenuContent() {
            return menuContent;
        }

        public void setEnabled(boolean b) {
            this.setDisable(!b);
            this.setOpacity(b ? 1 : 0.33);
        }

        public void setChildMenu(MenuBox menu) {
            MenuItem back = new MenuItem("<--");
            menu.getChildren().add(0, back);

            back.setOnMouseClicked(evt -> {
                switchMenuTo(MenuItem.this.menuParent);
            });

            this.setOnMouseClicked(event -> {
                switchMenuTo(menu);
            });
        }
    }

    private static class VideoMode {
        private int width, height;
        private float ratio;

        public VideoMode(int w, int h, float ratio) {
            this.width = w;
            this.height = h;
            this.ratio = ratio;
        }
    }
}
