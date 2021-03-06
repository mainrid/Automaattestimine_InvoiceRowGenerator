package invoice;

import java.math.BigDecimal;
import java.util.Date;

class InvoiceRow {

    public final BigDecimal amount;
    public final Date date;

    public InvoiceRow(BigDecimal amount, Date date) {
        this.amount = amount;
        this.date = date;
    }
    
    public BigDecimal getAmount(){
    	return this.amount;
    }
    
    public Date getDate(){
    	return this.date;
    }
}