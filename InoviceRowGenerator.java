package invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InoviceRowGenerator {


    private InvoiceRowDao invoiceRowDao;

    public void generateRowsFor(BigDecimal amount, Date start, Date end) {
    	
        // kood, mis read (InvoiceRow) genereerib ja salvestab, tuleb siia.
    	
    	List<InvoiceRow> invoiceRows= new ArrayList<InvoiceRow>();
    	List<Date> paymentDates= getPaymentDates(start, end);    	
    
    	int divideCounter= paymentDates.size();
    	BigDecimal remainingAmount= amount;
    
    	if(amount.compareTo(BigDecimal.ZERO)<=0 ){
    		return;    		
    	}
    	    	
    	for (int i = 0; i < paymentDates.size(); i++) {   
    		if(remainingAmount.subtract(new BigDecimal(String.valueOf(3))).compareTo(new BigDecimal(String.valueOf(3)))<0){
    			InvoiceRow ir = new InvoiceRow(remainingAmount, paymentDates.get(i));
    			invoiceRows.add(ir);
    			break;
        	}else{
    		int monthPayment= calculateMonthPayment(remainingAmount, divideCounter);
			InvoiceRow ir = new InvoiceRow(new BigDecimal(String.valueOf(monthPayment)), paymentDates.get(i));
			invoiceRows.add(ir);
			
			remainingAmount = remainingAmount.subtract(new BigDecimal(String.valueOf(monthPayment)));
			divideCounter--;
        	}
			
		}
    	saveInoiceRows(invoiceRows);
    	    	
    }
    
    private int calculateMonthPayment(BigDecimal remainingAmount, int divideCounter){    	
    	int calculatedPayment = remainingAmount.divide(new BigDecimal(String.valueOf(divideCounter)), 2, RoundingMode.HALF_UP).intValue();
    	if (calculatedPayment<3){
    		return 3;
    	}
    	return calculatedPayment;
    }
    
    
    private void saveInoiceRows(List<InvoiceRow> invoiceRows) {
    	for (InvoiceRow row : invoiceRows) {
    		invoiceRowDao.save(row);
    	}
    }
    
	public List<Date> getPaymentDates(Date start, Date end){
		if(start.after(end)){
			throw new IllegalArgumentException("Start date should not be after end date.");
		}
		
		ArrayList<Date> dates= new ArrayList<Date>();
		Date currentDate= start;
		
		while (currentDate.before(end)) {
			dates.add(currentDate);
			currentDate= getNextMonthFirstDate(currentDate);
			
		}		
		return dates;
	}

	private Date getNextMonthFirstDate(Date currentDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

}
