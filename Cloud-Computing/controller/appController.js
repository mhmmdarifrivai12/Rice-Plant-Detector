const dbConnection = require("../config/config");
const dosisObat = require("../utils/dataObat");

const kalkulasiDosis = (req, res) => {
  const { luasLahan, penyakit } = req.body;

  if (!luasLahan || isNaN(luasLahan) || !penyakit || !dosisObat[penyakit]) {
    return res.status(400).json({ error: "Input invalid" });
  }

  const { obat, kandunganBahan, dosis } = dosisObat[penyakit];
  const jumlahObat = luasLahan * dosis;
  const hasil = {
    luasLahan,
    penyakit,
    obat,
    kandunganBahan,
    jumlahObat,
  };

  res.status(200).json({ status: true, data: hasil });
};

const getAllHistory = (req, res) => {
  const sql = "SELECT* FROM predictions";
  dbConnection.query(sql,(err,result)=>{
    if(err){
      return res.status(500).json({status:false,message:err.message});
    }
    return res.status(200).json({status:true, data:result});
  })
};

const getHistory = (req, res) => {
  const userId = req.user.id;

  const sql = "SELECT * FROM predictions WHERE user_id = ? ORDER BY created_at DESC";

  dbConnection.query(sql, [userId], (err, rows) => {
    if (err) {
      console.error("Error:", err);
      return res.status(500).json({ message: "Internal server error" });
    }
    if (rows.length === 0) {
      return res.status(404).json({ message: "History not found" });
    }
    res.json({
      status: true,
      message: "History successfully",
      data: rows,
    });
  });
};

const deleteHistory = async (req, res) => {
  const { id } = req.params;

  try {
    const sql = "DELETE FROM predictions WHERE id = ?";
    const [result] = await dbConnection.promise().query(sql, [id]);

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "History not found" });
    }

    res.status(200).json({
      status: true,
      message: "History successfully deleted",
    });
  } catch (error) {
    console.error("Error deleting history:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

module.exports = { kalkulasiDosis, getHistory, deleteHistory, getAllHistory };
