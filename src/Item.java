import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item {

        private String name;
        private Date dateStart;
        private Date dateEnd;
        private String details;
        private String material;
        private int status;
        private String optionalInfo;


        private static List<Item> items = new ArrayList<>();

        public static void addEvent(Item event) {
            items.add(event);
        }

        public static List<Item> getEvents() {
            return items;
        }


        public Item(String name, String material, Date dateStart, Date dateEnd, int status, String optionalInfo) {
            this.name = name;
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
            this.details = " ";
            this.status = status;
            this.material = material;
            this.optionalInfo= optionalInfo;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDateStart() {
            return dateStart;
        }

        public void setDateStart(Date dateStart) {
            this.dateStart = dateStart;
        }

        public Date getDateEnd() {
            return dateEnd;
        }

        public void setDateEnd(Date dateEnd) {
            this.dateEnd = dateEnd;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getOptionalInfo() {
            return optionalInfo;
        }

        public void setOptionalInfo(String optionalInfo) {
            this.optionalInfo = optionalInfo;
        }

        public static void setEvents(List<Item> events) {
            Item.items = events;

        }

        public String getDateStr() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateFormat.format(dateStart);
        }
}
