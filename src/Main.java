import java.util.logging.*;

public class Main {

    public static void main(String[] args) throws Inspector.IllegalPackageException, Inspector.StolenPackageException {
        //Thieves
        MailPackage mypackage = new MailPackage("England", "Russia", new Package("Soap", 128));
        MailPackage mypackage1 = new MailPackage("England", "Russia", new Package("Fish", 10));
        MailPackage mypackage2 = new MailPackage("England", "Russia", new Package("Gold", 1024));
        MailPackage ston = new MailPackage("England", "Russia", new Package("stones", 0));
        Thief Mike = new Thief(90);
        Thief Serg = new Thief(40);
        Inspector Mark = new Inspector();
       // Mark.processMail(ston);

         Logger LOG = Logger.getLogger("java.logging.MailMessage");
         LOG.setLevel(Level.WARNING);
        Spy Bond007 = new Spy(LOG);
        MailService[] crazypeople = {Mike, Serg, Bond007};
        UntrustworthyMailWorker Pechkin = new UntrustworthyMailWorker(crazypeople);
        Mike.processMail(mypackage);
        Serg.processMail(mypackage1);
        Serg.processMail(mypackage2);
        Pechkin.processMail(mypackage);
        System.out.println("Mike stolen " + Mike.getStolenValue());
        System.out.println("Serg stolen " + Serg.getStolenValue());

    }

    /*
    Интерфейс: сущность, которую можно отправить по почте.
    У такой сущности можно получить от кого и кому направляется письмо.
    */
    public static interface Sendable {

        String getFrom();

        String getTo();
    }

    /*
    Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
    */
    public static interface MailService {
        public static final String AUSTIN_POWERS = "Austin Powers";
        public static final String WEAPONS = "weapons";
        public static final String BANNED_SUBSTANCE = "banned substance";
        Sendable processMail(Sendable mail) throws Inspector.IllegalPackageException, Inspector.StolenPackageException;
    }

    /*
    Абстрактный класс,который позволяет абстрагировать логику хранения
    источника и получателя письма в соответствующих полях класса.
    */
    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AbstractSendable that = (AbstractSendable) o;
            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;
            return true;
        }

    }

    /*
    Письмо, у которого есть текст, который можно получить с помощью метода `getMessage`
    */
    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MailMessage that = (MailMessage) o;
            if (message != null ? !message.equals(that.message) : that.message != null) return false;
            return true;
        }

    }

    /*
Посылка, содержимое которой можно получить с помощью метода `getContent`
*/
    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MailPackage that = (MailPackage) o;
            if (!content.equals(that.content)) return false;
            return true;
        }

    }

    /*
Класс, который задает посылку. У посылки есть текстовое описание содержимого и целочисленная ценность.
*/
    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Package aPackage = (Package) o;
            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;
            return true;
        }
    }

    /*
    Класс, в котором скрыта логика настоящей почты
    */
    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    public static class Thief implements MailService {
        int cost;
        int stolen = 0;

        public Thief(int cost) {
            this.cost = cost;
        }

        public Thief() {
            this.cost = 0;
        }

        public int getStolenValue() {
            return stolen;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage && ((MailPackage) mail).getContent().getPrice() >= cost) {
                int price = ((MailPackage) mail).content.getPrice();
                String content = ((MailPackage) mail).content.getContent();
                stolen = stolen + price;
                price = 0;
                content = "stones instead of " + content;
                MailPackage thmail = new MailPackage(mail.getFrom(), mail.getTo(), new Package(content, price));
                System.out.println(thmail.getFrom() + " " + thmail.getTo() + " *** " + thmail.content.content + " **** " + thmail.content.price);
                return thmail;

            }
            return mail;
        }
    }

    public static class Inspector implements MailService {


        @Override
        public Sendable processMail(Sendable mail) throws IllegalPackageException, StolenPackageException {
            String content = ((MailPackage) mail).content.getContent();
            if (mail instanceof MailPackage && (content == WEAPONS | content == BANNED_SUBSTANCE)) {
                throw new IllegalPackageException();
            } else if (mail instanceof MailPackage && content.contains("stones")) {
                System.out.println("STONES");
                throw new StolenPackageException();
            }
            return mail;
        }

        private class IllegalPackageException extends Throwable {



        public IllegalPackageException() {
        }
    }

        private class StolenPackageException extends Throwable {
            public StolenPackageException() {
            }
        }
    }

    public static class Spy implements MailService {

//        2.1) Если в качестве отправителя или получателя указан "Austin Powers", то нужно
//        написать в лог сообщение с уровнем WARN: Detected target mail correspondence: from {from} to {to} "{message}"
//        2.2) Иначе, необходимо написать в лог сообщение с уровнем INFO: Usual correspondence: from {from} to {to}

        Logger logger = null;
        Handler handler = null;

        public Spy(Logger logger) {
            this.logger = logger;
        }

        @Override
        public Sendable processMail(Sendable mail)  {
            if (mail instanceof MailMessage & (mail.getFrom().equals("Austin Powers") || mail.getTo().equals("Austin Powers"))){

                logger.log(Level.WARNING,"Detected target mail correspondence: from {0} to {1} \"{2}\"" , new Object [] {mail.getFrom(),mail.getTo(),((MailMessage) mail).message});
                handler.setLevel(Level.WARNING);
                handler.setFormatter(new XMLFormatter());
                logger.addHandler(handler);
                return mail;

            }
            else {logger.log(Level.INFO, "Usual correspondence: from {0} to {1}", new Object[] {mail.getFrom(), mail.getTo()});}
            return mail;
        }
    }

    public static class UntrustworthyMailWorker implements MailService {
        MailService[] mailServices;
        RealMailService realMailService = new RealMailService();

        public UntrustworthyMailWorker(MailService[] mailServices) {
            this.mailServices = mailServices;

        }

        public MailService getRealMailService() {
            return realMailService;
        }

        @Override
        public Sendable processMail(Sendable mail) throws Inspector.IllegalPackageException, Inspector.StolenPackageException {
            Sendable outmail = mail;
            for (int i = 0; i < mailServices.length; i++) {
                outmail = mailServices[i].processMail(outmail);

            }
            return realMailService.processMail(outmail);
        }
    }

}





